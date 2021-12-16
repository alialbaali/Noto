package com.noto.app.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.widget.LibraryListWidgetConfigActivity
import com.noto.app.widget.LibraryListWidgetService

fun Context.createLibraryListWidgetRemoteViews(
    appWidgetId: Int,
    isHeaderEnabled: Boolean,
    isEditWidgetButtonEnabled: Boolean,
    isAppIconEnabled: Boolean,
    isNewLibraryButtonEnabled: Boolean,
    widgetRadius: Int,
    isEmpty: Boolean,
): RemoteViews {
    return RemoteViews(packageName, R.layout.library_list_widget).apply {
        setViewVisibility(R.id.ll_header, if (isHeaderEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.iv_edit_widget, if (isEditWidgetButtonEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.iv_app_icon, if (isAppIconEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.fab, if (isNewLibraryButtonEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.tv_placeholder, if (isEmpty) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.lv, if (isEmpty) View.GONE else View.VISIBLE)
        setOnClickPendingIntent(R.id.iv_edit_widget, createEditWidgetButtonPendingIntent(appWidgetId))
        setOnClickPendingIntent(R.id.fab, createNewLibraryButtonPendingIntent(appWidgetId))
        setOnClickPendingIntent(R.id.iv_app_icon, createAppLauncherPendingIntent(appWidgetId))
        setOnClickPendingIntent(R.id.tv_app_name, createAppLauncherPendingIntent(appWidgetId))
        setRemoteAdapter(R.id.lv, createLibraryListServiceIntent(appWidgetId))
        setPendingIntentTemplate(R.id.lv, createLibraryItemPendingIntent(appWidgetId))
        setInt(R.id.ll, SetBackgroundResourceMethodName, widgetRadius.toWidgetShapeId())
        setInt(R.id.ll_header, SetBackgroundResourceMethodName, widgetRadius.toWidgetHeaderShapeId())
        if (isAppIconEnabled)
            setViewPadding(R.id.tv_app_name, 0.dp, 16.dp, 0.dp, 16.dp)
        else
            setViewPadding(R.id.tv_app_name, 16.dp, 16.dp, 16.dp, 16.dp)
    }
}

private fun Context.createEditWidgetButtonPendingIntent(appWidgetId: Int): PendingIntent? {
    val intent = Intent(
        AppWidgetManager.ACTION_APPWIDGET_CONFIGURE,
        null,
        this,
        LibraryListWidgetConfigActivity::class.java
    ).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
    }
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}

private fun Context.createNewLibraryButtonPendingIntent(appWidgetId: Int): PendingIntent? {
    val intent = Intent(
        Constants.Intent.ActionCreateLibrary,
        null,
        this,
        AppActivity::class.java
    )
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}

private fun Context.createLibraryListServiceIntent(appWidgetId: Int): Intent {
    return Intent(this, LibraryListWidgetService::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
    }
}

private fun Context.createLibraryItemPendingIntent(appWidgetId: Int): PendingIntent? {
    val intent = Intent(
        Constants.Intent.ActionOpenLibrary,
        null,
        this,
        AppActivity::class.java
    ).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
    }
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}
