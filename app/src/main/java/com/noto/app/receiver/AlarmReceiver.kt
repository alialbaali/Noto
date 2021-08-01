package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.model.NotoColor
import com.noto.app.note.NoteBody
import com.noto.app.note.NoteColor
import com.noto.app.note.NoteId
import com.noto.app.note.NoteTitle
import com.noto.app.util.createNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        intent?.let {

            val id = it.getIntExtra(NoteId, 0)
            val title = it.getStringExtra(NoteTitle) ?: String()
            val body = it.getStringExtra(NoteBody) ?: String()
            val notoColorOrdinal = it.getIntExtra(NoteColor, 0)
            val notoColor = NotoColor.values().first { it.ordinal == notoColorOrdinal }

            notificationManager.createNotification(context, id, title, body, notoColor)
        }
    }
}