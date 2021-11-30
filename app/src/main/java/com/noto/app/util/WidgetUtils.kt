package com.noto.app.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Layout

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

fun Layout.toWidgetViewId() = when (this) {
    Layout.Linear -> R.id.lv
    Layout.Grid -> R.id.gv
}