package com.noto.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.noto.app.domain.model.NotoColor
import com.noto.app.receiver.AlarmReceiver

const val NoteId = "noto_id"
const val NoteColor = "noto_color"
const val LibraryName = "library_name"
private const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT

fun AlarmManager.createAlarm(context: Context, noteId: Long, libraryName: String, notoColor: NotoColor, epochMilliseconds: Long) {

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(NoteId, noteId)
        putExtra(LibraryName, libraryName)
        putExtra(NoteColor, notoColor.ordinal)
    }

    val pendingIntent = PendingIntent.getBroadcast(context, noteId.toInt(), intent, PENDING_INTENT_FLAGS)

    AlarmManagerCompat.setExactAndAllowWhileIdle(this, AlarmManager.RTC_WAKEUP, epochMilliseconds, pendingIntent)
}

fun AlarmManager.cancelAlarm(context: Context, noteId: Long) {

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, noteId.toInt(), intent, PENDING_INTENT_FLAGS)

    cancel(pendingIntent)
}