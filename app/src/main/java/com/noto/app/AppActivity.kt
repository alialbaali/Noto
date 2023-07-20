package com.noto.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NamedNavArgument
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.noto.app.components.BaseActivity
import com.noto.app.databinding.AppActivityBinding
import com.noto.app.domain.model.*
import com.noto.app.filtered.FilteredItemModel
import com.noto.app.main.MainVaultFragment
import com.noto.app.util.*
import com.noto.app.vault.VaultTimeoutWorker
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

private val AppIntents = listOf(
    Intent.ACTION_SEND,
    Constants.Intent.ActionCreateFolder,
    Constants.Intent.ActionCreateNote,
    Constants.Intent.ActionOpenFolder,
    Constants.Intent.ActionOpenNote,
    Constants.Intent.ActionSettings,
)

class AppActivity : BaseActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }

    private val navController by lazy { navHostFragment.navController }

    private val workManager by lazy { WorkManager.getInstance(this) }

    private val notificationPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        viewModel.setNotificationPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.currentTheme != null) requestNotificationsPermissionIfRequired() // Check required otherwise the callback runs twice.
        notificationManager.createNotificationChannels(this)
        AppActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            setupNavigation()
            setupState()
            handleIntentContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.vaultTimeout.value == VaultTimeout.OnAppClose) {
            viewModel.closeVault()
            notificationManager.cancelVaultNotification()
        }
    }

    private fun setupNavigation() {
        if (intent?.action !in AppIntents) {
            when (val interfaceId = viewModel.mainInterfaceId.value) {
                FilteredItemModel.All.id -> inflateGraphAndSetStartDestination(
                    R.id.filteredFragment,
                    listOf(
                        navArgument(Constants.Model) {
                            defaultValue = FilteredItemModel.All
                        }
                    ),
                )

                FilteredItemModel.Recent.id -> inflateGraphAndSetStartDestination(
                    R.id.filteredFragment,
                    listOf(
                        navArgument(Constants.Model) {
                            defaultValue = FilteredItemModel.Recent
                        }
                    ),
                )

                FilteredItemModel.Scheduled.id -> inflateGraphAndSetStartDestination(
                    R.id.filteredFragment,
                    listOf(
                        navArgument(Constants.Model) {
                            defaultValue = FilteredItemModel.Scheduled
                        }
                    ),
                )

                FilteredItemModel.Archived.id -> inflateGraphAndSetStartDestination(
                    R.id.filteredFragment,
                    listOf(
                        navArgument(Constants.Model) {
                            defaultValue = FilteredItemModel.Archived
                        }
                    ),
                )

                AllFoldersId -> {
                    val args = listOf(navArgument(Constants.FolderId) { defaultValue = Folder.GeneralFolderId })
                    inflateGraphAndSetStartDestination(R.id.folderFragment, args)
                    if (navController.currentDestination?.id != R.id.mainFragment && viewModel.shouldNavigateToMainFragment) {
                        navController.navigate(R.id.mainFragment)
                        viewModel.setShouldNavigateToMainFragment(false)
                    }
                }

                else -> {
                    val args = listOf(navArgument(Constants.FolderId) { defaultValue = interfaceId })
                    inflateGraphAndSetStartDestination(R.id.folderFragment, args)
                }
            }
        } else {
            inflateGraphAndSetStartDestination(R.id.folderFragment)
        }
    }

    private fun handleIntentContent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val content = intent.getStringExtra(Intent.EXTRA_TEXT)
                showSelectFolderDialog(content)
            }

            Constants.Intent.ActionCreateFolder -> {
                if (navController.currentDestination?.id != R.id.newFolderFragment)
                    navController.navigate(R.id.newFolderFragment)
            }

            Constants.Intent.ActionCreateNote -> {
                val folderId = intent.getLongExtra(Constants.FolderId, 0)
                if (folderId == 0L) {
                    showSelectFolderDialog(null)
                } else {
                    val args = bundleOf(Constants.FolderId to folderId, Constants.SelectedNoteIds to longArrayOf())
                    navController.popBackStack(R.id.folderFragment, true)
                    navController.navigate(R.id.folderFragment, args)
                    navController.navigate(R.id.noteFragment, args)
                }
            }

            Constants.Intent.ActionOpenFolder -> {
                val folderId = intent.getLongExtra(Constants.FolderId, 0)
                val args = bundleOf(Constants.FolderId to folderId)
                navController.popBackStack(R.id.folderFragment, true)
                navController.navigate(R.id.folderFragment, args)
            }

            Constants.Intent.ActionOpenNote -> {
                val folderId = intent.getLongExtra(Constants.FolderId, 0)
                val noteId = intent.getLongExtra(Constants.NoteId, 0)
                val args = bundleOf(Constants.FolderId to folderId, Constants.NoteId to noteId, Constants.SelectedNoteIds to longArrayOf())
                navController.popBackStack(R.id.folderFragment, true)
                navController.navigate(R.id.folderFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }

            Constants.Intent.ActionSettings -> {
                if (navController.currentDestination?.id != R.id.settingsFragment)
                    navController.navigate(R.id.settingsFragment)
            }
        }
        /** Set [intent] to null, so that the code above doesn't run again after a configuration change.*/
        intent = null
    }

    private fun showSelectFolderDialog(content: String?) {
        navController.getBackStackEntry(R.id.folderFragment).savedStateHandle
            .getLiveData<Long>(Constants.FolderId)
            .observe(this) { folderId ->
                val options = navOptions {
                    popUpTo(R.id.folderFragment) {
                        inclusive = true
                    }
                }
                val args = bundleOf(Constants.FolderId to folderId, Constants.Body to content, Constants.SelectedNoteIds to longArrayOf())
                navController.navigate(R.id.folderFragment, args, options)
                navController.navigate(R.id.noteFragment, args)
            }
        if (navController.currentDestination?.id != R.id.selectFolderDialogFragment) {
            val args = bundleOf(Constants.FilteredFolderIds to longArrayOf(), Constants.Title to stringResource(R.string.select_folder))
            navController.navigate(R.id.selectFolderDialogFragment, args)
        }
    }

    private fun AppActivityBinding.setupState() {
        viewModel.icon
            .onEach { icon -> if (icon != viewModel.currentIcon.await()) setupIcon(icon) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.lastVersion,
            navController.destinationAsFlow(),
        ) { lastVersion, _ ->
            if (lastVersion != Release.Version.Current.format())
                if (navController.currentDestination?.id != R.id.whatsNewDialogFragment)
                    navController.navigate(R.id.whatsNewDialogFragment)
        }.launchIn(lifecycleScope)

        viewModel.isVaultOpen
            .onEach { isVaultOpen ->
                if (isVaultOpen)
                    notificationManager.sendVaultNotification(this@AppActivity)
                else
                    notificationManager.cancelVaultNotification()
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.isVaultOpen,
            viewModel.vaultTimeout,
            viewModel.scheduledVaultTimeout,
        ) { isVaultOpen, vaultTimeout, scheduledVaultTimeout ->
            if (vaultTimeout != scheduledVaultTimeout) {
                workManager.cancelAllWorkByTag(Constants.VaultTimeout)
                if (isVaultOpen)
                    when (vaultTimeout) {
                        VaultTimeout.After1Hour -> {
                            workManager.enqueue(createVaultTimeoutWorkRequest(1, TimeUnit.HOURS))
                            viewModel.setScheduledVaultTimeout(VaultTimeout.After1Hour)
                        }

                        VaultTimeout.After4Hours -> {
                            workManager.enqueue(createVaultTimeoutWorkRequest(4, TimeUnit.HOURS))
                            viewModel.setScheduledVaultTimeout(VaultTimeout.After4Hours)
                        }

                        VaultTimeout.After12Hours -> {
                            workManager.enqueue(createVaultTimeoutWorkRequest(12, TimeUnit.HOURS))
                            viewModel.setScheduledVaultTimeout(VaultTimeout.After12Hours)
                        }

                        else -> viewModel.setScheduledVaultTimeout(null)
                    }
            }
        }.launchIn(lifecycleScope)

        this@AppActivity.navHostFragment
            .childFragmentManager
            .registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                        super.onFragmentDestroyed(fm, f)
                        if (f is MainVaultFragment)
                            if (viewModel.vaultTimeout.value == VaultTimeout.Immediately) {
                                viewModel.closeVault()
                                notificationManager.cancelVaultNotification()
                            }
                    }
                },
                false
            )
    }

    private fun createVaultTimeoutWorkRequest(duration: Long, timeUnit: TimeUnit) = OneTimeWorkRequestBuilder<VaultTimeoutWorker>()
        .setInitialDelay(duration, timeUnit)
        .addTag(Constants.VaultTimeout)
        .build()

    private fun inflateGraphAndSetStartDestination(startDestinationId: Int, args: List<NamedNavArgument> = emptyList()) {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            .apply {
                this.setStartDestination(startDestinationId)
                args.forEach { this.addArgument(it.name, it.argument) }
            }
        navController.setGraph(graph, null)
    }

    @SuppressLint("RestrictedApi")
    private fun setupIcon(icon: Icon) {
        // Disable current icon.
        packageManager?.setComponentEnabledSetting(
            enabledComponentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )

        // Enable new icon.
        packageManager?.setComponentEnabledSetting(
            getComponentNameForIcon(icon),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )

        updatePinnedShortcuts()
    }

    @SuppressLint("RestrictedApi")
    private fun updatePinnedShortcuts() {
        ShortcutManagerCompat.getShortcuts(this, ShortcutManagerCompat.FLAG_MATCH_PINNED)
            .map {
                val intent = it.intent.setComponent(enabledComponentName)
                ShortcutInfoCompat.Builder(it)
                    .setActivity(enabledComponentName)
                    .setIntent(intent)
                    .build()
            }
            .also { shortcuts ->
                ShortcutManagerCompat.updateShortcuts(this, shortcuts)
            }
    }

    private fun requestNotificationsPermissionIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionStatus = ContextCompat.checkSelfPermission(this@AppActivity, Manifest.permission.POST_NOTIFICATIONS)
            if (notificationPermissionStatus == PackageManager.PERMISSION_DENIED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}