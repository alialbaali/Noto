package com.noto.app

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.noto.app.databinding.AppActivityBinding
import com.noto.app.domain.model.Language
import com.noto.app.util.Constants
import com.noto.app.util.createNotificationChannel
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AppActivity : BaseActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager.createNotificationChannel()
        AppActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            setupState()
            handleIntentContent()
        }
    }

    private fun AppActivityBinding.handleIntentContent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)
                ?.let { content -> showSelectLibraryDialog(content) }
            Constants.Intent.ActionCreateLibrary -> navController.navigate(R.id.newLibraryDialogFragment)
            Constants.Intent.ActionCreateNote -> {
                val libraryId = intent.getLongExtra(Constants.LibraryId, 0)
                if (libraryId == 0L) {
                    showSelectLibraryDialog(null)
                } else {
                    val args = bundleOf(Constants.LibraryId to libraryId)
                    navController.navigate(R.id.libraryFragment, args)
                    navController.navigate(R.id.noteFragment, args)
                }
            }
            Intent.ACTION_EDIT -> {
                val libraryId = intent.getLongExtra(Constants.LibraryId, 0)
                val noteId = intent.getLongExtra(Constants.NoteId, 0)
                val args = bundleOf(Constants.LibraryId to libraryId, Constants.NoteId to noteId)
                navController.navigate(R.id.libraryFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }
            Intent.ACTION_CREATE_DOCUMENT -> {
                val libraryId = intent.getLongExtra(Constants.LibraryId, 0)
                val args = bundleOf(Constants.LibraryId to libraryId)
                navController.navigate(R.id.libraryFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }
            Constants.Intent.ActionOpenLibrary -> {
                val libraryId = intent.getLongExtra(Constants.LibraryId, 0)
                val args = bundleOf(Constants.LibraryId to libraryId)
                navController.popBackStack(R.id.libraryFragment, true)
                navController.navigate(R.id.libraryFragment, args)
            }
            Constants.Intent.ActionOpenNote -> {
                val libraryId = intent.getLongExtra(Constants.LibraryId, 0)
                val noteId = intent.getLongExtra(Constants.NoteId, 0)
                val args = bundleOf(Constants.LibraryId to libraryId, Constants.NoteId to noteId)
                navController.popBackStack(R.id.libraryFragment, true)
                navController.navigate(R.id.libraryFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }
        }
    }

    private fun AppActivityBinding.showSelectLibraryDialog(content: String?) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Long>(Constants.LibraryId)
            ?.observe(this@AppActivity) { libraryId ->
                val args = bundleOf(Constants.LibraryId to libraryId, Constants.Body to content)
                navController.navigate(R.id.libraryFragment, args)
                navController.navigate(R.id.noteFragment, args)
            }
        val args = bundleOf(Constants.LibraryId to 0L)
        navController.navigate(R.id.selectLibraryDialogFragment, args)
    }

    private fun AppActivityBinding.setupState() {
        viewModel.language
            .onEach { language -> setupLanguage(language) }
            .launchIn(lifecycleScope)
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
        }
        if (resources.configuration.locale != locale) {
            Locale.setDefault(locale)
            resources.configuration.locale = locale
            resources.configuration.setLayoutDirection(locale)
            resources.updateConfiguration(resources.configuration, resources.displayMetrics)
            recreate()
        }
    }
}