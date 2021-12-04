package com.noto.app.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.widget.LibraryListWidgetProvider
import com.noto.app.widget.NoteListWidgetProvider

fun Context.createAppLauncherPendingIntent(appWidgetId: Int): PendingIntent? {
    val intent = Intent(this, AppActivity::class.java)
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}

fun Int.toWidgetShapeId() = when (this) {
    8 -> R.drawable.widget_shape_small
    16 -> R.drawable.widget_shape_medium
    else -> R.drawable.widget_shape_large
}

fun Int.toWidgetHeaderShapeId() = when (this) {
    8 -> R.drawable.widget_header_shape_small
    16 -> R.drawable.widget_header_shape_medium
    else -> R.drawable.widget_header_shape_large
}

fun Context.updateAllWidgetsData() {
    val libraryListComponentName = ComponentName(this, LibraryListWidgetProvider::class.java)
    val noteListComponentName = ComponentName(this, NoteListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val libraryListWidgetIds = appWidgetManager.getAppWidgetIds(libraryListComponentName)
    val noteListWidgetIds = appWidgetManager.getAppWidgetIds(noteListComponentName)
    val allWidgetIds = libraryListWidgetIds + noteListWidgetIds
    appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds, R.id.lv)
}

fun Context.updateNoteListWidgets(libraryId: Long) {
    val componentName = ComponentName(this, NoteListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
    val intent = Intent(this, NoteListWidgetProvider::class.java).apply {
        putExtra(Constants.LibraryId, libraryId)
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}

fun Context.updateLibraryListWidgets() {
    val componentName = ComponentName(this, LibraryListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
    val intent = Intent(this, LibraryListWidgetProvider::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}
