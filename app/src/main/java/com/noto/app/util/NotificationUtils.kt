package com.noto.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.NotoColor

private const val CHANNEL_ID = "Noto Channel"
private const val CHANNEL_NAME = "Noto"

fun NotificationManager.createNotification(context: Context, id: Int, title: String, body: String, notoColor: NotoColor) {

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(body)
        .setColor(ResourcesCompat.getColor(context.resources, notoColor.toResource(), null))
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_REMINDER else null)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notify(id, notification)
}

fun NotificationManager.createNotificationChannel() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nc = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        nc.enableVibration(true)
        nc.lightColor = Color.RED
        createNotificationChannel(nc)
    }

}