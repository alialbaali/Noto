package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.domain.model.Layout
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants.Widget.AppIcon
import com.noto.app.util.Constants.Widget.EditButton
import com.noto.app.util.Constants.Widget.Header
import com.noto.app.util.Constants.Widget.Layout
import com.noto.app.util.Constants.Widget.NewItemButton
import com.noto.app.util.Constants.Widget.Radius
import com.noto.app.util.createLibraryListWidgetRemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val storage by inject<LocalStorage>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        appWidgetIds?.forEach { appWidgetId ->
            coroutineScope.launch {
                val remoteViews = context?.createLibraryListWidgetRemoteViews(
                    appWidgetId,
                    storage.getOrNull(appWidgetId.Layout).map { if (it == null) Layout.Linear else Layout.valueOf(it) }.first(),
                    storage.getOrNull(appWidgetId.Header).map { it?.toBoolean() ?: true }.first(),
                    storage.getOrNull(appWidgetId.EditButton).map { it?.toBoolean() ?: true }.first(),
                    storage.getOrNull(appWidgetId.AppIcon).map { it?.toBoolean() ?: true }.first(),
                    storage.getOrNull(appWidgetId.NewItemButton).map { it?.toBoolean() ?: true }.first(),
                    storage.getOrNull(appWidgetId.Radius).map { it?.toInt() ?: 16 }.first(),
                    libraryRepository.getLibraries().first().isEmpty(),
                )
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}