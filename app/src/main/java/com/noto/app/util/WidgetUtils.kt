package com.noto.app.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.noto.app.R
import com.noto.app.widget.FolderListWidgetProvider
import com.noto.app.widget.NoteListWidgetProvider

fun Context.createAppLauncherPendingIntent(appWidgetId: Int): PendingIntent? {
    val intent = Intent().setComponent(enabledComponentName)
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

fun Context.updateFolderWidget(widgetId: Int) {
    val appWidgetManager = AppWidgetManager.getInstance(this)
    // Needed to update the visibility of notes count in Folder items.
    appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.lv)
    val intent = Intent(this, FolderListWidgetProvider::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}

fun Context.updateNoteWidget(widgetId: Int) {
    val appWidgetManager = AppWidgetManager.getInstance(this)
    appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.lv)
    val intent = Intent(this, NoteListWidgetProvider::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}

fun Context.updateAllWidgetsData() {
    val folderListComponentName = ComponentName(this, FolderListWidgetProvider::class.java)
    val noteListComponentName = ComponentName(this, NoteListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val folderListWidgetIds = appWidgetManager.getAppWidgetIds(folderListComponentName)
    val noteListWidgetIds = appWidgetManager.getAppWidgetIds(noteListComponentName)
    val allWidgetIds = folderListWidgetIds + noteListWidgetIds
    appWidgetManager.notifyAppWidgetViewDataChanged(allWidgetIds, R.id.lv)
}

fun Context.updateNoteListWidgets() {
    val componentName = ComponentName(this, NoteListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
    val intent = Intent(this, NoteListWidgetProvider::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}

fun Context.updateFolderListWidgets() {
    val componentName = ComponentName(this, FolderListWidgetProvider::class.java)
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val widgetIds = appWidgetManager.getAppWidgetIds(componentName)
    val intent = Intent(this, FolderListWidgetProvider::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    }
    sendBroadcast(intent)
}
