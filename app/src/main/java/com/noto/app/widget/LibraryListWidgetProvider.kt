package com.noto.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Layout
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.PendingIntentFlags
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val storage by inject<LocalStorage>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            val intent = Intent(Constants.Intent.ActionOpenLibrary, null, context, AppActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
            }
            val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntentFlags)
            val serviceIntent = Intent(context, LibraryListWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
            }
            val layout = runBlocking {
                storage.get(Constants.LibraryListLayoutKey)
                    .filterNotNull()
                    .map { Layout.valueOf(it) }
                    .first()
            }
            val layoutId = when (layout) {
                Layout.Linear -> R.layout.library_list_widget
                Layout.Grid -> R.layout.library_grid_widget
            }
            val viewId = when (layout) {
                Layout.Linear -> R.id.lv
                Layout.Grid -> R.id.gv
            }
            val remoteViews = RemoteViews(context?.packageName, layoutId).apply {
                setRemoteAdapter(viewId, serviceIntent)
                setPendingIntentTemplate(viewId, pendingIntent)
            }
            appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}