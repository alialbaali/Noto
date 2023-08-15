package com.noto.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Note
import com.noto.app.vault.VaultReceiver

private const val RemindersChannelId = "Reminders"
private const val VaultChannelId = "Vault"
private const val QuickNoteChannelId = "Quick Note"
private const val VaultNotificationId = -1
private const val RequestCode = 0
private const val QuickNoteTimeout = 5000L

fun NotificationManager.sendReminderNotification(context: Context, folder: Folder, note: Note, icon: Icon?) {

    val pendingIntent = context.createNotificationPendingIntent(note.id, note.folderId)

    val style = NotificationCompat.BigTextStyle()
        .bigText(note.body.ifBlank { note.title })

    val notification = NotificationCompat.Builder(context, RemindersChannelId)
        .setContentTitle(note.title.ifBlank { note.body })
        .setContentText(note.body.ifBlank { note.title })
        .setContentIntent(pendingIntent)
        .setSubText(folder.getTitle(context))
        .setStyle(style)
        .setColor(context.colorResource(folder.color.toColorResourceId()))
        .setColorized(true)
        .setCategory(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Notification.CATEGORY_REMINDER else null)
        .setSmallIcon(icon?.toDrawableResourceId() ?: R.mipmap.ic_launcher_futuristic)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setGroup(folder.getTitle(context))
        .setGroupSummary(true)
        .build()

    notify(folder.getTitle(context), note.id.toInt(), notification)
}

fun NotificationManager.sendVaultNotification(context: Context) {

    val intent = Intent(context, VaultReceiver::class.java)
    val actionPendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntentFlags)
    val action = NotificationCompat.Action(null, context.stringResource(R.string.close_vault), actionPendingIntent)
    val pendingIntent = context.createVaultNotificationPendingIntent()

    val notification = NotificationCompat.Builder(context, VaultChannelId)
        .setContentTitle(context.stringResource(R.string.vault_is_open))
        .setContentIntent(pendingIntent)
        .addAction(action)
        .setCategory(Notification.CATEGORY_STATUS)
        .setSmallIcon(R.drawable.ic_round_shield_24)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    notify(VaultNotificationId, notification)
}

fun NotificationManager.sendQuickNoteNotification(context: Context, folder: Folder, note: Note, icon: Icon?) {

    val pendingIntent = context.createNotificationPendingIntent(note.id, note.folderId)

    val notification = NotificationCompat.Builder(context, QuickNoteChannelId)
        .setContentTitle(context.stringResource(R.string.note_is_saved, folder.getTitle(context)))
        .setContentIntent(pendingIntent)
        .setSubText(folder.getTitle(context))
        .setColor(context.colorResource(folder.color.toColorResourceId()))
        .setColorized(true)
        .setCategory(Notification.CATEGORY_STATUS)
        .setSmallIcon(icon?.toDrawableResourceId() ?: R.mipmap.ic_launcher_futuristic)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(null)
        .setSound(null)
        .setTimeoutAfter(QuickNoteTimeout)
        .setAutoCancel(true)
        .setGroup(folder.getTitle(context))
        .setGroupSummary(true)
        .build()

    notify(folder.getTitle(context), note.id.toInt(), notification)
}

private fun Context.createVaultNotificationPendingIntent(): PendingIntent? {
    return Intent(Constants.Intent.ActionOpenVault)
        .apply { component = enabledComponentName }
        .let { PendingIntent.getActivity(this, RequestCode, it, PendingIntentFlags) }
}

fun NotificationManager.cancelVaultNotification() = cancel(VaultNotificationId)

private fun Context.createNotificationPendingIntent(noteId: Long, folderId: Long): PendingIntent? {
    return Intent(Constants.Intent.ActionOpenNote)
        .apply {
            component = enabledComponentName
            putExtra(Constants.FolderId, folderId)
            putExtra(Constants.NoteId, noteId)
        }
        .let { PendingIntent.getActivity(this, RequestCode, it, PendingIntentFlags) }
}

fun NotificationManager.createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(RemindersChannelId, context.stringResource(R.string.reminders), NotificationManager.IMPORTANCE_HIGH)
            .also(this::createNotificationChannel)
        NotificationChannel(VaultChannelId, context.stringResource(R.string.vault), NotificationManager.IMPORTANCE_LOW)
            .also(this::createNotificationChannel)
        NotificationChannel(QuickNoteChannelId, context.stringResource(R.string.quick_note), NotificationManager.IMPORTANCE_HIGH)
            .apply {
                enableVibration(false)
                enableLights(false)
                setSound(null, null)
            }
            .also(this::createNotificationChannel)
    }
}