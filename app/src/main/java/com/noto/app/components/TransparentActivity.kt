package com.noto.app.components

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.noto.app.AppViewModel
import com.noto.app.util.createQuickNoteNotification
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransparentActivity : AppCompatActivity() {

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
                        notificationManager.createQuickNoteNotification(this, folder, note, icon)
                        finish()
                    }
                }
            }
        }
    }
}