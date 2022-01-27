package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.Constants
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
    var folderId: Long = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        folderId = intent?.getLongExtra(Constants.FolderId, 0) ?: 0
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        folderId.takeIf { it != 0L }?.let { folderId ->
            appWidgetIds?.forEach { appWidgetId ->
                coroutineScope.launch {
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