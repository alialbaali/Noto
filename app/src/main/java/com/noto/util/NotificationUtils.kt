package com.noto.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noto.R
import timber.log.Timber

private const val CHANNEL_ID = "Noto Channel"
private const val NOTIFICATION_ID = 0
private const val CHANNEL_NAME = "Noto"

fun NotificationManager.createNotification(title: String, body: String, context: Context) {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.ic_done_24dp)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    Timber.i("NOTIFICATION")

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nc = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        nc.enableVibration(true)
        nc.enableVibration(true)
        nc.lightColor = Color.RED
        nc.description = "DESCRIPTION"
        this.createNotificationChannel(nc)
    }

}