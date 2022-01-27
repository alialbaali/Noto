package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.createFolderListWidgetRemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FolderListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val settingsRepository by inject<SettingsRepository>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        appWidgetIds?.forEach { appWidgetId ->
            coroutineScope.launch {
                val remoteViews = context?.createFolderListWidgetRemoteViews(
                    appWidgetId,
                    settingsRepository.getIsWidgetHeaderEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetEditButtonEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetAppIconEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetNewItemButtonEnabled(appWidgetId).first(),
                    settingsRepository.getWidgetRadius(appWidgetId).first(),
                    folderRepository.getFolders().first().isEmpty(),
                )
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}