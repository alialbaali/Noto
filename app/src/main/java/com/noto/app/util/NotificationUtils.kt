package com.noto.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.noto.app.R
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note

private const val CHANNEL_ID = "Noto Channel"
private const val CHANNEL_NAME = "Reminders"

fun NotificationManager.createNotification(context: Context, library: Library, note: Note) {

    val pendingIntent = context.createNotificationPendingIntent(note.id, note.libraryId)

    val style = NotificationCompat.BigTextStyle()
        .bigText(note.body)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(note.title)
        .setContentText(note.body)
        .setContentIntent(pendingIntent)
        .setSubText(library.title)
        .setStyle(style)
        .setColor(context.resources.colorResource(library.color.toResource()))
        .setColorized(true)
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_REMINDER else null)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setGroup(library.title)
        .setGroupSummary(true)
        .build()

    notify(library.title, note.id.toInt(), notification)
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
        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            enableVibration(true)
        }
        createNotificationChannel(notificationChannel)
    }
}