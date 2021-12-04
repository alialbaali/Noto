package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.Constants.Widget.AppIcon
import com.noto.app.util.Constants.Widget.EditButton
import com.noto.app.util.Constants.Widget.Header
import com.noto.app.util.Constants.Widget.NewItemButton
import com.noto.app.util.Constants.Widget.Radius
import com.noto.app.util.createNoteListWidgetRemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val storage by inject<LocalStorage>()
    var libraryId: Long = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        libraryId = intent?.getLongExtra(Constants.LibraryId, 0) ?: 0
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        libraryId.takeIf { it != 0L }?.let { libraryId ->
            appWidgetIds?.forEach { appWidgetId ->
                coroutineScope.launch {
                    val remoteViews = context?.createNoteListWidgetRemoteViews(
                        appWidgetId,
                        storage.getOrNull(appWidgetId.Header).map { it?.toBoolean() ?: true }.first(),
                        storage.getOrNull(appWidgetId.EditButton).map { it?.toBoolean() ?: true }.first(),
                        storage.getOrNull(appWidgetId.AppIcon).map { it?.toBoolean() ?: true }.first(),
                        storage.getOrNull(appWidgetId.NewItemButton).map { it?.toBoolean() ?: true }.first(),
                        storage.getOrNull(appWidgetId.Radius).map { it?.toInt() ?: 16 }.first(),
                        libraryRepository.getLibraryById(libraryId).first(),
                        noteRepository.getNotesByLibraryId(libraryId).first().isEmpty(),
                    )
                    appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
                }
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}