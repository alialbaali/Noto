package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.Constants
import com.noto.app.util.createNotification
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val noteRepository by inject<NoteRepository>()

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        intent?.let {

            val folderId = it.getLongExtra(Constants.FolderId, 0)
            val noteId = it.getLongExtra(Constants.NoteId, 0)

            runBlocking {
                val folder = folderRepository.getFolderById(folderId)
                    .firstOrNull()
                val note = noteRepository.getNoteById(noteId)
                    .firstOrNull()

                if (note != null && folder != null && context != null) {
                    notificationManager?.createNotification(context, folder, note)
                    noteRepository.updateNote(note.copy(reminderDate = null))
                }
            }
        }
    }
}