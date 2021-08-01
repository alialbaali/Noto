package com.noto.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.note.*
import com.noto.app.receiver.AlarmReceiver

fun AlarmManager.createAlarm(context: Context, note: Note, notoColor: NotoColor, epochMilliseconds: Long) {

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra(NoteId, note.id)
        putExtra(NoteTitle, note.title)
        putExtra(NoteBody, note.body)
        putExtra(NoteColor, notoColor.ordinal)
    }

    val pendingIntent = PendingIntent.getBroadcast(context, note.id.toInt(), intent, PENDING_INTENT_FLAGS)

    setAlarm(AlarmManager.RTC_WAKEUP, epochMilliseconds, pendingIntent)
}

fun AlarmManager.cancelAlarm(context: Context, noteId: Int) {

    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, noteId, intent, PENDING_INTENT_FLAGS)

    cancel(pendingIntent)
}