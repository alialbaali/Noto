package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.*
import com.noto.app.util.Constants.Widget.NotesCount
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListRemoteViewsFactory(private val context: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val storage by inject<LocalStorage>()
    private val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        ?: AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var libraries: List<Pair<Library, Int>>
    private var isShowNotesCount: Boolean = true

    override fun onCreate() {}

    override fun onDataSetChanged() = runBlocking {
        val sortingType = storage.get(Constants.LibraryListSortingTypeKey)
            .filterNotNull()
            .map { LibraryListSortingType.valueOf(it) }
            .first()
        val sortingOrder = storage.get(Constants.LibraryListSortingOrderKey)
            .filterNotNull()
            .map { SortingOrder.valueOf(it) }
            .first()
        libraries = libraryRepository.getLibraries()
            .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
                libraries.map { library -> library to librariesNotesCount.first { it.libraryId == library.id }.notesCount }
            }
            .filterNotNull()
            .first()
            .sorted(sortingType, sortingOrder)
            .sortedByDescending { it.first.isPinned }
        isShowNotesCount = storage.getOrNull(appWidgetId.NotesCount)
            .map { it?.toBoolean() ?: true }
            .first()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = libraries.count()

    override fun getViewAt(position: Int): RemoteViews {
        val entry = libraries[position]
        val library = entry.first
        val notesCount = entry.second
        val color = context.colorResource(library.color.toResource())
        val intent = Intent(Constants.Intent.ActionOpenLibrary, null, context, AppActivity::class.java).apply {
            putExtra(Constants.LibraryId, library.id)
        }
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_library_item).apply {
            val notesCountText = context.pluralsResource(R.plurals.notes_count, notesCount, notesCount)
            setTextViewText(R.id.tv_library_notes_count, notesCountText)
            setOnClickFillInIntent(R.id.ll, intent)
            setContentDescription(R.id.ll, library.title)
            setViewVisibility(R.id.tv_library_notes_count, if (isShowNotesCount) View.VISIBLE else View.GONE)
            setTextViewText(R.id.tv_library_title, library.title)
            setTextColor(R.id.tv_library_title, color)
            setTextColor(R.id.tv_library_notes_count, color)
            setInt(R.id.iv_library_color, SetColorFilterMethodName, color)
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = libraries[position].first.id

    override fun hasStableIds(): Boolean = true
}