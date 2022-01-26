package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.createLibraryListWidgetRemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val settingsRepository by inject<SettingsRepository>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        appWidgetIds?.forEach { appWidgetId ->
            coroutineScope.launch {
                val remoteViews = context?.createLibraryListWidgetRemoteViews(
                    appWidgetId,
                    settingsRepository.getIsWidgetHeaderEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetEditButtonEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetAppIconEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetNewItemButtonEnabled(appWidgetId).first(),
                    settingsRepository.getWidgetRadius(appWidgetId).first(),
                    libraryRepository.getLibraries().first().isEmpty(),
                )
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}