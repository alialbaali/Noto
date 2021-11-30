package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.noto.app.util.Constants
import com.noto.app.util.createNoteListWidgetRemoteViews
import com.noto.app.util.filterSelected
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class NoteListWidgetProvider : AppWidgetProvider(), KoinComponent {

    var libraryId: Long = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        libraryId = intent?.getLongExtra(Constants.LibraryId, 0) ?: 0
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            libraryId.takeIf { it != 0L }?.let { libraryId ->
                val viewModel by inject<NoteListWidgetConfigViewModel> { parametersOf(appWidgetId) }
                viewModel.getData(libraryId)
                val remoteViews = context?.createNoteListWidgetRemoteViews(
                    appWidgetId,
                    viewModel.widgetLayout.value,
                    viewModel.isWidgetHeaderEnabled.value,
                    viewModel.isEditWidgetButtonEnabled.value,
                    viewModel.isAppIconEnabled.value,
                    viewModel.isNewLibraryButtonEnabled.value,
                    viewModel.widgetRadius.value,
                    viewModel.library.value,
                    viewModel.notes.value.isEmpty(),
                    viewModel.labels.value.filterSelected(),
                )
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

}