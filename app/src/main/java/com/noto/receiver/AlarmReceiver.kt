package com.noto.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.domain.interactor.noto.GetNoto
import com.noto.noto.NOTO_BODY
import com.noto.noto.NOTO_ID
import com.noto.noto.NOTO_TITLE
import com.noto.util.createChannel
import com.noto.util.sendNotification
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {

        val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createChannel(context)

        intent?.let {

            val id = intent.getIntExtra(NOTO_ID, 0)
            val title = intent.getStringExtra(NOTO_TITLE) ?: String()
            val body = intent.getStringExtra(NOTO_BODY) ?: String()

            nm.sendNotification(context, id, title, body)

        }


    }

}