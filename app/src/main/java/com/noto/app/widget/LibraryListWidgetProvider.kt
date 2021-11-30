package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.noto.app.util.createLibraryListWidgetRemoteViews
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LibraryListWidgetProvider : AppWidgetProvider(), KoinComponent {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            val viewModel by inject<LibraryListWidgetConfigViewModel> { parametersOf(appWidgetId) }
            val remoteViews = context?.createLibraryListWidgetRemoteViews(
                appWidgetId,
                viewModel.widgetLayout.value,
                viewModel.isWidgetHeaderEnabled.value,
                viewModel.isEditWidgetButtonEnabled.value,
                viewModel.isAppIconEnabled.value,
                viewModel.isNewLibraryButtonEnabled.value,
                viewModel.widgetRadius.value,
                viewModel.libraries.value.isEmpty(),
            )
            appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}