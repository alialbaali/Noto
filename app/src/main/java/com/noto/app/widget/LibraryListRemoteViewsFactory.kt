package com.noto.app.widget

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Library
import com.noto.app.util.Constants
import com.noto.app.util.colorResource
import com.noto.app.util.pluralsResource
import com.noto.app.util.toResource

private const val SetColorFilterMethodName = "setColorFilter"

class LibraryListRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent?,
    private val libraries: List<Library>,
    private val isShowNotesCount: Boolean,
    private val countNotes: (Long) -> Int,
) : RemoteViewsService.RemoteViewsFactory {

    override fun onCreate() {}

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount(): Int = libraries.count()

    override fun getViewAt(position: Int): RemoteViews {
        val library = libraries[position]
        val color = context.colorResource(library.color.toResource())
        val intent = Intent(Constants.Intent.ActionOpenLibrary, null, context, AppActivity::class.java).apply {
            putExtra(Constants.LibraryId, library.id)
        }
        val notesCount = countNotes(library.id)
        val notesCountText = context.pluralsResource(R.plurals.notes_count, notesCount, notesCount)
        val remoteViews = RemoteViews(context.packageName, R.layout.library_item).apply {
            setOnClickFillInIntent(R.id.ll, intent)
            setContentDescription(R.id.ll, library.title)
            setViewVisibility(R.id.ib_drag, View.GONE)
            setViewVisibility(R.id.tv_library_notes_count, if (isShowNotesCount) View.VISIBLE else View.GONE)
            setTextViewText(R.id.tv_library_title, library.title)
            setTextViewText(R.id.tv_library_notes_count, notesCountText)
            setTextColor(R.id.tv_library_title, color)
            setTextColor(R.id.tv_library_notes_count, color)
            setInt(R.id.iv_library_color, SetColorFilterMethodName, color)
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = libraries[position].id

    override fun hasStableIds(): Boolean = true
}