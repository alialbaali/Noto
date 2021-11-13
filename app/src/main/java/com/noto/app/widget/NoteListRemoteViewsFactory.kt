package com.noto.app.widget

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import org.koin.core.component.KoinComponent

class NoteListRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent?,
    val library: Library,
    val notes: List<Pair<Note, List<Label>>>,
) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    override fun onCreate() {}

    override fun onDataSetChanged() {}

    override fun onDestroy() {}

    override fun getCount(): Int = notes.count()

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_item).apply {
            val pair = notes[position]
            val intent = Intent(Constants.Intent.ActionOpenNote, null, context, AppActivity::class.java).apply {
                putExtra(Constants.LibraryId, library.id)
                putExtra(Constants.NoteId, pair.first.id)
            }
            val color = context.colorResource(library.color.toResource())
            removeAllViews(R.id.ll_labels)
            pair.second.forEach { label ->
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_label_item).apply {
                    setContentDescription(R.id.fl, label.title)
                    setTextViewText(R.id.tv_label, label.title)
                    setInt(R.id.iv_library_color, SetColorFilterMethodName, color)
                }
                addView(R.id.ll_labels, remoteViews)
            }
            setOnClickFillInIntent(R.id.ll, intent)
            setContentDescription(R.id.ll, pair.first.title)
            setTextViewText(R.id.tv_note_title, pair.first.title)
            setTextViewText(R.id.tv_creation_date, context.stringResource(R.string.created, pair.first.creationDate.format(context)))
            setViewVisibility(R.id.tv_creation_date, if (library.isShowNoteCreationDate) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.tv_note_title, if (pair.first.title.isNotBlank()) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.ll_labels, if (pair.second.isNotEmpty()) View.VISIBLE else View.GONE)
            setViewPadding(R.id.tv_note_title, 0, 0, 0, if (pair.first.body.isBlank() || library.notePreviewSize == 0) 0.dp else 4.dp)
            setViewPadding(R.id.tv_note_body, 0, if (pair.first.title.isBlank()) 0.dp else 4.dp, 0, 0)
            if (pair.first.title.isBlank() && library.notePreviewSize == 0) {
                setTextViewText(R.id.tv_note_body, pair.first.body.takeLines(1))
                setViewVisibility(R.id.tv_note_body, View.VISIBLE)
            } else {
                setTextViewText(R.id.tv_note_body, pair.first.body.takeLines(library.notePreviewSize))
                setViewVisibility(R.id.tv_note_body, if (library.notePreviewSize != 0 && pair.first.body.isNotBlank()) View.VISIBLE else View.GONE)
            }
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = notes[position].first.id

    override fun hasStableIds(): Boolean = true
}