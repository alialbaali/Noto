package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.R
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.*
import com.noto.app.folder.NoteItemModel
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
    private lateinit var notes: List<NoteItemModel>
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
                it.mapToNoteItemModel(labels, noteLabels)
                    .filterByLabels(selectedLabels, filteringType)
                    .sortedWith(NoteItemModel.Comparator(folder.sortingOrder, folder.sortingType))
                    .sortedByDescending { it.note.isPinned }
            }
            .first()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = notes.count()

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_item).apply {
            val model = notes[position]
            val intent = Intent(Constants.Intent.ActionOpenNote, null).apply {
                putExtra(Constants.FolderId, folder.id)
                putExtra(Constants.NoteId, model.note.id)
                component = context.enabledComponentName
            }
            val color = context.colorResource(folder.color.toColorResourceId())
            removeAllViews(R.id.ll_labels)
            model.labels.forEach { label ->
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_note_label_item).apply {
                    setContentDescription(R.id.fl, label.title)
                    setTextViewText(R.id.tv_label, label.title)
                    setInt(R.id.iv_folder_color, SetColorFilterMethodName, color)
                }
                addView(R.id.ll_labels, remoteViews)
            }
            setOnClickFillInIntent(R.id.ll, intent)
            setContentDescription(R.id.ll, model.note.title)
            setTextViewText(R.id.tv_note_title, model.note.title)
            setTextViewText(R.id.tv_creation_date, context.stringResource(R.string.created, model.note.creationDate.format(context)))
            setViewVisibility(R.id.tv_creation_date, if (folder.isShowNoteCreationDate) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.tv_note_title, if (model.note.title.isNotBlank()) View.VISIBLE else View.GONE)
            setViewVisibility(R.id.ll_labels, if (model.labels.isNotEmpty()) View.VISIBLE else View.GONE)
            setViewPadding(R.id.tv_note_title, 0, 0, 0, if (model.note.body.isBlank() || folder.notePreviewSize == 0) 0.dp else 4.dp)
            setViewPadding(R.id.tv_note_body, 0, if (model.note.title.isBlank()) 0.dp else 4.dp, 0, 0)
            if (model.note.title.isBlank() && folder.notePreviewSize == 0) {
                setTextViewText(R.id.tv_note_body, model.note.body.takeLines(1))
                setViewVisibility(R.id.tv_note_body, View.VISIBLE)
            } else {
                setTextViewText(R.id.tv_note_body, model.note.body.takeLines(folder.notePreviewSize))
                setViewVisibility(R.id.tv_note_body, if (folder.notePreviewSize != 0 && model.note.body.isNotBlank()) View.VISIBLE else View.GONE)
            }
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = notes[position].note.id

    override fun hasStableIds(): Boolean = true
}