package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.createNoteListWidgetRemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val settingsRepository by inject<SettingsRepository>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        appWidgetIds?.forEach { appWidgetId ->
            coroutineScope.launch {
                settingsRepository.getWidgetFolderId(appWidgetId).first().takeIf { it != 0L }?.let { folderId ->
                    val remoteViews = context?.createNoteListWidgetRemoteViews(
                        appWidgetId,
                        settingsRepository.getIsWidgetHeaderEnabled(appWidgetId).first(),
                        settingsRepository.getIsWidgetEditButtonEnabled(appWidgetId).first(),
                        settingsRepository.getIsWidgetAppIconEnabled(appWidgetId).first(),
                        settingsRepository.getIsWidgetNewItemButtonEnabled(appWidgetId).first(),
                        settingsRepository.getWidgetRadius(appWidgetId).first(),
                        folderRepository.getFolderById(folderId).first(),
                        noteRepository.getNotesByFolderId(folderId).first().isEmpty(),
                    )
                    appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
                }
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}