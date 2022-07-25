package com.noto.app.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.noto.app.R
import com.noto.app.domain.model.Icon
import com.noto.app.widget.FolderListWidgetConfigActivity
import com.noto.app.widget.FolderListWidgetService

fun Context.createFolderListWidgetRemoteViews(
    appWidgetId: Int,
    isHeaderEnabled: Boolean,
    isEditWidgetButtonEnabled: Boolean,
    isAppIconEnabled: Boolean,
    isNewFolderButtonEnabled: Boolean,
    widgetRadius: Int,
    isEmpty: Boolean,
    icon: Icon,
): RemoteViews {
    val activityName = icon.toActivityAliasName(isAppActivityIconEnabled())
    return RemoteViews(packageName, R.layout.folder_list_widget).apply {
        setViewVisibility(R.id.ll_header, if (isHeaderEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.iv_edit_widget, if (isEditWidgetButtonEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.iv_app_icon, if (isAppIconEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.fab, if (isNewFolderButtonEnabled) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.tv_placeholder, if (isEmpty) View.VISIBLE else View.GONE)
        setViewVisibility(R.id.lv, if (isEmpty) View.GONE else View.VISIBLE)
        setOnClickPendingIntent(R.id.iv_edit_widget, createEditWidgetButtonPendingIntent(appWidgetId))
        setOnClickPendingIntent(R.id.fab, createNewFolderButtonPendingIntent(appWidgetId, activityName))
        setOnClickPendingIntent(R.id.iv_app_icon, createAppLauncherPendingIntent(appWidgetId, activityName))
        setOnClickPendingIntent(R.id.tv_app_name, createAppLauncherPendingIntent(appWidgetId, activityName))
        setRemoteAdapter(R.id.lv, createFolderListServiceIntent(appWidgetId, widgetRadius))
        setPendingIntentTemplate(R.id.lv, createFolderItemPendingIntent(appWidgetId, activityName))
        setInt(R.id.ll, SetBackgroundResourceMethodName, widgetRadius.toWidgetShapeId())
        setInt(R.id.ll_header, SetBackgroundResourceMethodName, widgetRadius.toWidgetHeaderShapeId())
        setInt(R.id.iv_app_icon, SetImageResource, icon.toResource())
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
        FolderListWidgetConfigActivity::class.java
    ).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
    }
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}

private fun Context.createNewFolderButtonPendingIntent(appWidgetId: Int, activityName: String): PendingIntent? {
    val intent = Intent(Constants.Intent.ActionCreateFolder, null)
        .setClassName(this, activityName)
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}

private fun Context.createFolderListServiceIntent(appWidgetId: Int, widgetRadius: Int): Intent {
    return Intent(this, FolderListWidgetService::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
        putExtra(Constants.WidgetRadius, widgetRadius)
    }
}

private fun Context.createFolderItemPendingIntent(appWidgetId: Int, activityName: String): PendingIntent? {
    val intent = Intent(Constants.Intent.ActionOpenFolder, null).apply {
        setClassName(this@createFolderItemPendingIntent, activityName)
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
    }
    return PendingIntent.getActivity(this, appWidgetId, intent, PendingIntentFlags)
}
