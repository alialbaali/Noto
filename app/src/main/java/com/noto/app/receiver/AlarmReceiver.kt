package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.LibraryId
import com.noto.app.util.NoteId
import com.noto.app.util.createNotification
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        intent?.let {

            val noteId = it.getLongExtra(NoteId, 0)
            val libraryId = it.getLongExtra(LibraryId, 0)

            runBlocking {
                val library = libraryRepository.getLibraryById(libraryId)
                    .firstOrNull()
                val note = noteRepository.getNoteById(noteId)
                    .firstOrNull()

                if (note != null && library != null) {
                    notificationManager.createNotification(context, library, note)
                    noteRepository.updateNote(note.copy(reminderDate = null))
                }
            }
        }
    }
}