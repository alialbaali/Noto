package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.NoteColor
import com.noto.app.util.NoteId
import com.noto.app.util.createNotification
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val noteRepository by inject<NoteRepository>()

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        intent?.let {

            val id = it.getLongExtra(NoteId, 0)
            val notoColorOrdinal = it.getIntExtra(NoteColor, 0)
            val notoColor = NotoColor.values().first { it.ordinal == notoColorOrdinal }

            runBlocking {
                noteRepository.getNoteById(id)
                    .firstOrNull()
                    ?.let { note ->
                        notificationManager.createNotification(context, note, notoColor)
                        noteRepository.updateNote(note.copy(reminderDate = null))
                    }
            }
        }
    }
}