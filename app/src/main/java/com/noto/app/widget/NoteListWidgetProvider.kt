package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.domain.repository.*
import com.noto.app.folder.NoteItemModel
import com.noto.app.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val labelRepository by inject<LabelRepository>()
    private val noteLabelRepository by inject<NoteLabelRepository>()
    private val settingsRepository by inject<SettingsRepository>()

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        appWidgetIds?.forEach { appWidgetId ->
            coroutineScope.launch {
                val folderId = settingsRepository.getWidgetFolderId(appWidgetId).filter { it != 0L }.first()
                val folder = folderRepository.getFolderById(folderId).first()
                val filteringType = settingsRepository.getWidgetFilteringType(appWidgetId)
                    .filterNotNull()
                    .first()
                val labels = labelRepository.getLabelsByFolderId(folderId)
                    .filterNotNull()
                    .first()
                val noteLabels = noteLabelRepository.getNoteLabels()
                    .filterNotNull()
                    .first()
                val labelIds = settingsRepository.getWidgetSelectedLabelIds(appWidgetId, folderId).first()
                val selectedLabels = labels.filter { it.id in labelIds }
                val isEmpty = noteRepository.getNotesByFolderId(folderId)
                    .filterNotNull()
                    .map {
                        it.mapToNoteItemModel(labels, noteLabels)
                            .filterByLabels(selectedLabels, filteringType)
                            .sortedWith(NoteItemModel.Comparator(folder.sortingOrder, folder.sortingType))
                            .sortedByDescending { it.note.isPinned }
                    }
                    .first()
                    .isEmpty()
                val remoteViews = context?.createNoteListWidgetRemoteViews(
                    appWidgetId,
                    settingsRepository.getIsWidgetHeaderEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetEditButtonEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetAppIconEnabled(appWidgetId).first(),
                    settingsRepository.getIsWidgetNewItemButtonEnabled(appWidgetId).first(),
                    settingsRepository.getWidgetRadius(appWidgetId).first(),
                    folder,
                    isEmpty,
                    noteRepository.getNotesByFolderId(folderId).first().isEmpty(),
                    settingsRepository.icon.first(),
                )
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}