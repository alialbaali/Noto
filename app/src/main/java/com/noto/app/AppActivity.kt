package com.noto.app

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.Release
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.main.MainVaultFragment
import com.noto.app.settings.VaultTimeoutWorker
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class AppActivity : BaseActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }

    private val navController by lazy { navHostFragment.navController }

    private val workManager by lazy { WorkManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager.createNotificationChannels(this)
        AppActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
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
            Constants.Intent.ActionCreateFolder -> navController.navigate(R.id.newFolderDialogFragment)
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
            else -> {
                lifecycleScope.launch {
                    val folderId = viewModel.mainFolderId.first()
                    val args = bundleOf(Constants.FolderId to folderId)
                    val options = navOptions {
                        popUpTo(R.id.folderFragment) {
                            inclusive = true
                        }
                    }
                    navController.navigate(R.id.folderFragment, args, options)
                }
            }
        }
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

        viewModel.lastVersion
            .onEach {
                if (it != Release.CurrentVersion)
                    if (navController.currentDestination?.id != R.id.whatsNewDialogFragment)
                        navController.navigate(R.id.whatsNewDialogFragment)
            }
            .launchIn(lifecycleScope)

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
        }
        if (resources.configuration.locale != locale) {
            Locale.setDefault(locale)
            resources.configuration.locale = locale
            resources.configuration.setLayoutDirection(locale)
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            recreate()
        }
    }

    private fun createVaultTimeoutWorkRequest(duration: Long, timeUnit: TimeUnit) = OneTimeWorkRequestBuilder<VaultTimeoutWorker>()
        .setInitialDelay(duration, timeUnit)
        .addTag(Constants.VaultTimeout)
        .build()
}