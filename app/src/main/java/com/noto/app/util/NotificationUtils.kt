package com.noto.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.receiver.VaultReceiver

private const val RemindersChannelId = "Reminders"
private const val VaultChannelId = "Vault"
private const val VaultNotificationId = -1
private const val RequestCode = 0

fun NotificationManager.createNotification(context: Context, folder: Folder, note: Note) {

    val pendingIntent = context.createNotificationPendingIntent(note.id, note.folderId)

    val style = NotificationCompat.BigTextStyle()
        .bigText(note.body.ifBlank { note.title })

    val notification = NotificationCompat.Builder(context, RemindersChannelId)
        .setContentTitle(note.title.ifBlank { note.body })
        .setContentText(note.body.ifBlank { note.title })
        .setContentIntent(pendingIntent)
        .setSubText(folder.getTitle(context))
        .setStyle(style)
        .setColor(context.colorResource(folder.color.toResource()))
        .setColorized(true)
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_REMINDER else null)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setGroup(folder.getTitle(context))
        .setGroupSummary(true)
        .build()

    notify(folder.getTitle(context), note.id.toInt(), notification)
}

fun NotificationManager.createVaultNotification(context: Context) {

    val intent = Intent(context, VaultReceiver::class.java)
    val actionPendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntentFlags)
    val action = NotificationCompat.Action(null, context.stringResource(R.string.close_vault), actionPendingIntent)
    val pendingIntent = context.createVaultNotificationPendingIntent()

    val notification = NotificationCompat.Builder(context, VaultChannelId)
        .setContentTitle(context.stringResource(R.string.vault_is_open))
        .setContentIntent(pendingIntent)
        .addAction(action)
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_STATUS else null)
        .setSmallIcon(R.drawable.ic_round_shield_24)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    notify(VaultNotificationId, notification)
}

private fun Context.createVaultNotificationPendingIntent(): PendingIntent {
    return NavDeepLinkBuilder(this)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.mainVaultFragment)
        .createPendingIntent()
}

fun NotificationManager.cancelVaultNotification() = cancel(VaultNotificationId)

private fun Context.createNotificationPendingIntent(noteId: Long, folderId: Long): PendingIntent {
    val args = bundleOf(Constants.FolderId to folderId, Constants.NoteId to noteId)
    return NavDeepLinkBuilder(this)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.noteFragment)
        .setArguments(args)
        .createPendingIntent()
}

fun NotificationManager.createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(RemindersChannelId, context.stringResource(R.string.reminders), NotificationManager.IMPORTANCE_HIGH)
            .also(this::createNotificationChannel)
        NotificationChannel(VaultChannelId, context.stringResource(R.string.vault), NotificationManager.IMPORTANCE_LOW)
            .also(this::createNotificationChannel)
    }
}