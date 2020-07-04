package com.noto.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noto.R
import timber.log.Timber

private const val CHANNEL_ID = "Noto Channel"
private const val CHANNEL_NAME = "Noto"

fun NotificationManager.sendNotification(context: Context, id: Int, title: String, body: String) {

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(body)
//        .setlar
//        .setLargeIcon(BitmapFactory.decodeResource(context.resources,  R.mipmap.))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(id, builder.build())
}

fun NotificationManager.createChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nc = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        nc.enableVibration(true)
        nc.lightColor = Color.RED
        nc.description = "DESCRIPTION"
        createNotificationChannel(nc)
    }

}