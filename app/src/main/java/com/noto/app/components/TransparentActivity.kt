package com.noto.app.components

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import com.noto.app.AppViewModel
import com.noto.app.note.QuickNoteDialogFragment
import com.noto.app.util.Constants
import com.noto.app.util.isValid
import com.noto.app.util.sendQuickNoteNotification
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransparentActivity : BaseActivity() {

    private val viewModel by viewModel<AppViewModel>()

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent?.action) {
            Intent.ACTION_PROCESS_TEXT -> {
                val content = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                if (content != null) {
                    viewModel.createQuickNote(content) { folder, note ->
                        val icon = viewModel.icon.value
                        notificationManager.sendQuickNoteNotification(this, folder, note, icon)
                        finish()
                    }
                }
            }
            Constants.Intent.ActionQuickNote -> {
                if (!viewModel.isQuickNoteDialogCreated) {
                    viewModel.setIsQuickNoteDialogCreated()
                    QuickNoteDialogFragment { folder, note ->
                        val icon = viewModel.icon.value
                        if (note.isValid) notificationManager.sendQuickNoteNotification(this, folder, note, icon)
                        if (!isChangingConfigurations) finish()
                    }.apply {
                        arguments = bundleOf(Constants.FolderId to viewModel.quickNoteFolderId.value)
                        show(supportFragmentManager, null)
                    }
                }
            }
        }
    }
}