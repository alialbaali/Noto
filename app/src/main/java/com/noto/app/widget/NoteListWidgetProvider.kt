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
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.util.Constants
import com.noto.app.util.PendingIntentFlags
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()

    var libraryId: Long = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        libraryId = intent?.getLongExtra(Constants.LibraryId, 0) ?: 0
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            libraryId.takeIf { it != 0L }?.let { libraryId ->
                val intent = Intent(Constants.Intent.ActionOpenNote, null, context, AppActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
                }
                val pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntentFlags)
                val serviceIntent = Intent(context, NoteListWidgetService::class.java).apply {
                    putExtra(Constants.LibraryId, libraryId)
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    data = Uri.parse(this.toUri(Intent.URI_INTENT_SCHEME))
                }
                val library = runBlocking {
                    libraryRepository.getLibraryById(libraryId)
                        .filterNotNull()
                        .first()
                }
                val layoutId = when (library.layout) {
                    Layout.Linear -> R.layout.note_list_widget
                    Layout.Grid -> R.layout.note_grid_widget
                }
                val viewId = when (library.layout) {
                    Layout.Linear -> R.id.lv
                    Layout.Grid -> R.id.gv
                }
                val remoteViews = RemoteViews(context?.packageName, layoutId).apply {
                    setRemoteAdapter(viewId, serviceIntent)
                    setPendingIntentTemplate(viewId, pendingIntent)
                    setTextViewText(R.id.tv_library_title, library.title)
                }
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

}