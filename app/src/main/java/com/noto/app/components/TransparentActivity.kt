package com.noto.app.components

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.noto.app.AppViewModel
import com.noto.app.note.QuickNoteDialogFragment
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransparentActivity : BaseActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (appViewModel.currentTheme == null) return
        setupState()
        handleIntentContent()
    }

    private fun setupState() {
        // When new Quick Note is created, send a notification, and destroy the activity.
        combine(
            viewModel.quickNoteFolder,
            viewModel.quickNote
                .filterNotNull(),
            viewModel.icon,
        ) { folder, note, icon ->
            if (note.isValid) notificationManager.sendQuickNoteNotification(this, folder, note, icon)
            finish()
        }.launchIn(lifecycleScope)
    }

    @Suppress("NewApi")
    private fun handleIntentContent() {
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> { // Text selection then Quick Note.
                intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                    ?.let { content -> viewModel.createQuickNote(content) }
            }

            Constants.Intent.ActionQuickNote -> { // Notification Center then Quick Note.
                navigateToQuickNoteDialogFragment()
                supportFragmentManager.setFragmentResultListener(Constants.QuickNote, this) { _, result ->
                    result.getLong(Constants.NoteId).also(viewModel::setQuickNote)
                }
            }
        }
    }

    private fun navigateToQuickNoteDialogFragment() = lifecycleScope.launch {
        val folderId = viewModel.quickNoteFolder.first().id
        val args = bundleOf(Constants.FolderId to folderId)
        supportFragmentManager.commitNow { add(QuickNoteDialogFragment::class.java, args, null) }
    }

}