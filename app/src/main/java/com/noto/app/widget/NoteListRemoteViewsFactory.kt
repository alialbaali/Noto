package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.*
import com.noto.app.util.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListRemoteViewsFactory(private val context: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val labelRepository by inject<LabelRepository>()
    private val noteLabelRepository by inject<NoteLabelRepository>()
    private val settingsRepository by inject<SettingsRepository>()
    private val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        ?: AppWidgetManager.INVALID_APPWIDGET_ID
    private val folderId = intent?.getLongExtra(Constants.FolderId, 0) ?: 0
    private lateinit var folder: Folder
    private lateinit var labelIds: List<Long>
    private lateinit var notes: List<NoteWithLabels>
    private lateinit var filteringType: FilteringType

    override fun onCreate() {}

    override fun onDataSetChanged() = runBlocking {
        folder = folderRepository.getFolderById(folderId)
            .filterNotNull()
            .first()
        labelIds = settingsRepository.getWidgetSelectedLabelIds(appWidgetId, folderId).first()
        val labels = labelRepository.getLabelsByFolderId(folderId)
            .filterNotNull()
            .first()
        val selectedLabels = labels.filter { it.id in labelIds }
        val noteLabels = noteLabelRepository.getNoteLabels()
            .filterNotNull()
            .first()
        filteringType = settingsRepository.getWidgetFilteringType(appWidgetId)
            .filterNotNull()
            .first()
        notes = noteRepository.getNotesByFolderId(folderId)
            .filterNotNull()
            .map {
                it.mapWithLabels(labels, noteLabels)
                    .filterSelectedLabels(selectedLabels, filteringType)
                    .sorted(folder.sortingType, folder.sortingOrder)
                    .sortedByDescending { it.first.isPinned }
            }
            .first()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = notes.count()

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_item).apply {
            val pair = notes[position]
            val intent = Intent(Constants.Intent.ActionOpenNote, null, context, AppActivity::class.java).apply {
                putExtra(Constants.FolderId, folder.id)
                putExtra(Constants.NoteId, pair.first.id)
            }
            val color = context.colorResource(folder.color.toResource())
            removeAllViews(R.id.ll_labels)
            pair.second.forEach { label ->
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_label_item).apply {
                    setContentDescription(R.id.fl, label.title)
                    setTextViewText(R.id.tv_label, label.title)
                    setInt(R.id.iv_folder_color, SetColorFilterMethodName, color)
                }
                addView(R.id.ll_labels, remoteViews)
            }
            setOnClickFillInIntent(R.id.ll, intent)
            setContentDescription(R.id.ll, pair.first.title)
            setTextViewText(R.id.tv_note_title, pair.first.title)
            setTextViewText(R.id.tv_creation_date, context.stringResource(R.string.created, pair.first.creationDate.format(context)))
            setViewVisibility(R.id.tv_creation_date, if (folder.isShowNoteCreationDate) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.tv_note_title, if (pair.first.title.isNotBlank()) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.ll_labels, if (pair.second.isNotEmpty()) View.VISIBLE else View.GONE)
            setViewPadding(R.id.tv_note_title, 0, 0, 0, if (pair.first.body.isBlank() || folder.notePreviewSize == 0) 0.dp else 4.dp)
            setViewPadding(R.id.tv_note_body, 0, if (pair.first.title.isBlank()) 0.dp else 4.dp, 0, 0)
            if (pair.first.title.isBlank() && folder.notePreviewSize == 0) {
                setTextViewText(R.id.tv_note_body, pair.first.body.takeLines(1))
                setViewVisibility(R.id.tv_note_body, View.VISIBLE)
            } else {
                setTextViewText(R.id.tv_note_body, pair.first.body.takeLines(folder.notePreviewSize))
                setViewVisibility(R.id.tv_note_body, if (folder.notePreviewSize != 0 && pair.first.body.isNotBlank()) View.VISIBLE else View.GONE)
            }
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = notes[position].first.id

    override fun hasStableIds(): Boolean = true
}