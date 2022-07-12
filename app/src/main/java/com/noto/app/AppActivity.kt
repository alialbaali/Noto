package com.noto.app

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.noto.app.databinding.AppActivityBinding
import com.noto.app.domain.model.*
import com.noto.app.main.MainVaultFragment
import com.noto.app.settings.VaultTimeoutWorker
import com.noto.app.util.*
import io.ktor.http.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

private val AppIntents = listOf(
    Intent.ACTION_SEND,
    Intent.ACTION_PROCESS_TEXT,
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

    private val currentFragment
        get() = navHostFragment.parentFragmentManager.fragments
            .firstOrNull { it != null && it.isVisible }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        viewModel.userStatus
            .onEach { userStatus ->
                when (userStatus) {
                    UserStatus.None, UserStatus.LoggedIn -> if (intent?.action !in AppIntents) {
                        when (val interfaceId = viewModel.mainInterfaceId.value) {
                            AllNotesItemId -> inflateGraphAndSetStartDestination(R.id.allNotesFragment)
                            RecentNotesItemId -> inflateGraphAndSetStartDestination(R.id.recentNotesFragment)
                            AllFoldersId -> {
                                val args = bundleOf(Constants.FolderId to Folder.GeneralFolderId)
                                inflateGraphAndSetStartDestination(R.id.folderFragment, args)
                                if (navController.currentDestination?.id != R.id.mainFragment && viewModel.shouldNavigateToMainFragment) {
                                    navController.navigate(R.id.mainFragment)
                                    viewModel.shouldNavigateToMainFragment = false
                                }
                            }
                            else -> {
                                val args = bundleOf(Constants.FolderId to interfaceId)
                                inflateGraphAndSetStartDestination(R.id.folderFragment, args)
                            }
                        }
                    } else {
                        inflateGraphAndSetStartDestination(R.id.folderFragment)
                    }
                    UserStatus.NotLoggedIn -> inflateGraphAndSetStartDestination(R.id.startFragment)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun AppActivityBinding.handleIntentContent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                val content = intent.getStringExtra(Intent.EXTRA_TEXT)
                showSelectFolderDialog(content)
            }
            Intent.ACTION_PROCESS_TEXT -> {
                val content = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                showSelectFolderDialog(content)
            }
            Constants.Intent.ActionCreateFolder -> navController.navigate(R.id.newFolderFragment)
            Constants.Intent.ActionCreateNote -> {
                val folderId = intent.getLongExtra(Constants.FolderId, 0)
                if (folderId == 0L) {
                    showSelectFolderDialog(null)
                } else {
                    val args = bundleOf(Constants.FolderId to folderId)
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
                val args = bundleOf(Constants.FolderId to folderId, Constants.NoteId to noteId)
                navController.popBackStack(R.id.folderFragment, true)
                navController.navigate(R.id.folderFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }
            Constants.Intent.ActionSettings -> {
                if (navController.currentDestination?.id != R.id.settingsFragment)
                    navController.navigate(R.id.settingsFragment)
            }
            Intent.ACTION_VIEW -> handleActionViewIntent(intent)
        }
        /** Set [intent] to null, so that the code above doesn't run again after a configuration change.*/
        intent = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_VIEW) handleActionViewIntent(intent)
    }

    private fun handleActionViewIntent(intent: Intent) {
        viewModel.handleIntentUri(
            uri = intent.data ?: Uri.EMPTY,
            onResult = { stringId ->
                if (navController.currentDestination?.id == R.id.verifyEmailDialogFragment) navController.navigateUp()
                if (navController.currentDestination?.id == R.id.changeEmailDialogFragment) navController.navigateUp()
                if (stringId != null) currentFragment?.view?.snackbar(stringId)
            },
        )
    }

    private fun AppActivityBinding.showSelectFolderDialog(content: String?) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Long>(Constants.FolderId)
            ?.observe(this@AppActivity) { folderId ->
                val options = navOptions {
                    popUpTo(R.id.folderFragment) {
                        inclusive = true
                    }
                }
                val args = bundleOf(Constants.FolderId to folderId, Constants.Body to content)
                navController.navigate(R.id.folderFragment, args, options)
                navController.navigate(R.id.noteFragment, args)
            }
        val args = bundleOf(Constants.FilteredFolderIds to longArrayOf())
        navController.navigate(R.id.selectFolderDialogFragment, args)
    }

    private fun AppActivityBinding.setupState() {
        viewModel.language
            .onEach { language -> setupLanguage(language) }
            .launchIn(lifecycleScope)

        if (!BuildConfig.DEBUG) {
            viewModel.icon
                .onEach { icon -> if (icon != viewModel.currentIcon.await()) setupIcon(icon) }
                .launchIn(lifecycleScope)
        }

        combine(
            viewModel.lastVersion,
            navController.destinationAsFlow(),
        ) { lastVersion, _ ->
            if (lastVersion != Release.Version.Current)
                if (navController.currentDestination?.id != R.id.whatsNewDialogFragment)
                    navController.navigate(R.id.whatsNewDialogFragment)
        }.launchIn(lifecycleScope)

        viewModel.isVaultOpen
            .onEach { isVaultOpen ->
                if (isVaultOpen)
                    notificationManager.createVaultNotification(this@AppActivity)
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

    @Suppress("DEPRECATION")
    private fun AppActivityBinding.setupLanguage(language: Language) {
        val locale = when (language) {
            Language.System -> Locale.getDefault()
            Language.English -> Locale("en")
            Language.Turkish -> Locale("tr")
            Language.Arabic -> Locale("ar")
            Language.Indonesian -> Locale("in")
            Language.Russian -> Locale("ru")
            Language.Tamil -> Locale("ta")
            Language.Spanish -> Locale("es")
            Language.French -> Locale("fr")
            Language.German -> Locale("de")
        }
        if (resources.configuration.locale != locale) {
            Locale.setDefault(locale)
            resources.configuration.locale = locale
            resources.configuration.setLayoutDirection(locale)
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
//            recreate()
        }
    }

    private fun createVaultTimeoutWorkRequest(duration: Long, timeUnit: TimeUnit) =
        OneTimeWorkRequestBuilder<VaultTimeoutWorker>()
            .setInitialDelay(duration, timeUnit)
            .addTag(Constants.VaultTimeout)
            .build()

    private fun inflateGraphAndSetStartDestination(startDestination: Int, args: Bundle? = null) {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            .apply { this.startDestination = startDestination }
        navController.setGraph(graph, args)
    }

    private fun setupIcon(icon: Icon) {
        // Disable activity icon.
        packageManager?.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )

        // Disable old icons.
        Icon.values().forEach { oldIcon ->
            val activityAliasName = oldIcon.toActivityAliasName()
            val componentName = ComponentName(this@AppActivity, activityAliasName)
            packageManager?.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )
        }

        // Enable new icon.
        val activityAliasName = icon.toActivityAliasName()
        val componentName = ComponentName(this@AppActivity, activityAliasName)
        packageManager?.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun Icon.toActivityAliasName() = when (this) {
        Icon.Futuristic -> "Futuristic"
        Icon.DarkRain -> "DarkRain"
        Icon.Airplane -> "Airplane"
        Icon.BlossomIce -> "BlossomIce"
        Icon.DarkAlpine -> "DarkAlpine"
        Icon.DarkSide -> "DarkSide"
        Icon.Earth -> "Earth"
        Icon.Fire -> "Fire"
        Icon.Purpleberry -> "Purpleberry"
        Icon.SanguineSun -> "SanguineSun"
    }.let { "com.noto.app.$it" }

}