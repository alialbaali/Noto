package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.note.*
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
import com.noto.app.util.createChannel
import com.noto.app.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createChannel(context)

        println(intent?.extras?.toString())

        intent?.let {

            val id = it.getIntExtra(NoteId, 0)
            val title = it.getStringExtra(NoteTitle) ?: String()
            val body = it.getStringExtra(NoteBody) ?: String()
            val notoColorOrdinal = it.getIntExtra(NoteColor, 0)
            val notoIconOrdinal = it.getIntExtra(NoteIcon, 0)
            val notoColor = NotoColor.values().first { it.ordinal == notoColorOrdinal }
            val notoIcon = NotoIcon.values().first { it.ordinal == notoIconOrdinal }

            nm.sendNotification(context, id, title, body, notoColor, notoIcon)

        }


    }

}