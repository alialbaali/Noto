package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListRemoteViewsFactory(private val context: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val settingsRepository by inject<SettingsRepository>()
    private val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        ?: AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var libraries: List<Pair<Folder, Int>>
    private var isShowNotesCount: Boolean = true

    override fun onCreate() {}

    override fun onDataSetChanged() = runBlocking {
        val sortingType = settingsRepository.sortingType.first()
        val sortingOrder = settingsRepository.sortingOrder.first()
        isShowNotesCount = settingsRepository.getWidgetNotesCount(appWidgetId).first()
        libraries = libraryRepository.getLibraries()
            .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
                libraries.map { library ->
                    val notesCount = librariesNotesCount.firstOrNull { it.folderId == library.id }?.notesCount ?: 0
                    library to notesCount
                }
            }
            .filterNotNull()
            .first()
            .sorted(sortingType, sortingOrder)
            .sortedByDescending { it.first.isPinned }
            .sortedByDescending { it.first.isInbox }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = libraries.count()

    override fun getViewAt(position: Int): RemoteViews {
        val entry = libraries[position]
        val library = entry.first
        val notesCount = entry.second
        val color = context.colorResource(library.color.toResource())
        val iconResource = if (library.isInbox) R.drawable.ic_round_inbox_24 else R.drawable.ic_round_library_24
        val intent = Intent(Constants.Intent.ActionOpenLibrary, null, context, AppActivity::class.java).apply {
            putExtra(Constants.LibraryId, library.id)
        }
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_library_item).apply {
            setTextViewText(R.id.tv_library_notes_count, notesCount.toString())
            setTextViewText(R.id.tv_library_title, library.getTitle(context))
            setContentDescription(R.id.ll, library.getTitle(context))
            setTextColor(R.id.tv_library_title, color)
            setTextColor(R.id.tv_library_notes_count, color)
            setImageViewResource(R.id.iv_library_icon, iconResource)
            setInt(R.id.iv_library_icon, SetColorFilterMethodName, color)
            setOnClickFillInIntent(R.id.ll, intent)
            setViewVisibility(R.id.tv_library_notes_count, if (isShowNotesCount) View.VISIBLE else View.GONE)
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = libraries[position].first.id

    override fun hasStableIds(): Boolean = true
}