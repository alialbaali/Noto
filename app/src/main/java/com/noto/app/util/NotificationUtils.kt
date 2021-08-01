package com.noto.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.noto.app.R
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor

private const val CHANNEL_ID = "Noto Channel"
private const val CHANNEL_NAME = "Noto"

fun NotificationManager.createNotification(context: Context, note: Note, notoColor: NotoColor) {

    val pendingIntent = context.createNotificationPendingIntent(note.id, note.libraryId)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(note.title)
        .setContentText(note.body)
        .setContentIntent(pendingIntent)
        .setColor(ResourcesCompat.getColor(context.resources, notoColor.toResource(), null))
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_REMINDER else null)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notify(note.libraryId.toString(), note.id.toInt(), notification)
}

private fun Context.createNotificationPendingIntent(noteId: Long, libraryId: Long): PendingIntent {
    val args = bundleOf("library_id" to libraryId, "note_id" to noteId)
    return NavDeepLinkBuilder(this)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.noteFragment)
        .setArguments(args)
        .createPendingIntent()
}

fun NotificationManager.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.description = "Reminders"
        createNotificationChannel(notificationChannel)
    }
}