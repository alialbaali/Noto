package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.note.*
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.app.util.createChannel
import com.noto.app.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createChannel(context)

        println(intent?.extras?.toString())

        intent?.let {

            val id = it.getIntExtra(NOTO_ID, 0)
            val title = it.getStringExtra(NOTO_TITLE) ?: String()
            val body = it.getStringExtra(NOTO_BODY) ?: String()
            val notoColorOrdinal = it.getIntExtra(NOTO_COLOR, 0)
            val notoIconOrdinal = it.getIntExtra(NOTO_ICON, 0)
            val notoColor = NotoColor.values().first { it.ordinal == notoColorOrdinal }
            val notoIcon = NotoIcon.values().first { it.ordinal == notoIconOrdinal }

            nm.sendNotification(context, id, title, body, notoColor, notoIcon)

        }


    }

}