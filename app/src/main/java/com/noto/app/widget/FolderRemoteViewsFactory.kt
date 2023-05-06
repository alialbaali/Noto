package com.noto.app.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FolderRemoteViewsFactory(private val context: Context, intent: Intent?) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val folderRepository by inject<FolderRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val settingsRepository by inject<SettingsRepository>()
    private val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        ?: AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var folders: List<Pair<Folder, Int>>
    private var isShowNotesCount: Boolean = true
    private val widgetRadius = intent?.getIntExtra(Constants.WidgetRadius, 0)

    override fun onCreate() {}

    override fun onDataSetChanged() = runBlocking {
        val sortingType = settingsRepository.sortingType.first()
        val sortingOrder = settingsRepository.sortingOrder.first()
        isShowNotesCount = settingsRepository.getWidgetNotesCount(appWidgetId).first()
        folders = folderRepository.getFolders()
            .combine(noteRepository.getFolderNotesCount()) { folders, foldersNotesCount ->
                folders.map { folder ->
                    val notesCount = foldersNotesCount.firstOrNull { it.folderId == folder.id }?.notesCount ?: 0
                    folder to notesCount
                }
            }
            .filterNotNull()
            .first()
            .sortedWith(Folder.Comparator(sortingOrder, sortingType))
            .sortedByDescending { it.first.isPinned }
            .sortedByDescending { it.first.isGeneral }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = folders.count()

    override fun getViewAt(position: Int): RemoteViews {
        val entry = folders[position]
        val folder = entry.first
        val notesCount = entry.second
        val color = context.colorResource(folder.color.toResource())
        val iconResource = if (folder.isGeneral) R.drawable.ic_round_folder_general_24 else R.drawable.ic_round_folder_24
        val intent = Intent(Constants.Intent.ActionOpenFolder, null).apply {
            putExtra(Constants.FolderId, folder.id)
            component = context.enabledComponentName
        }
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_folder_item).apply {
            setTextViewText(R.id.tv_folder_notes_count, notesCount.toString())
            setTextViewText(R.id.tv_folder_title, folder.getTitle(context))
            setContentDescription(R.id.ll, folder.getTitle(context))
            setContentDescription(R.id.iv_folder_icon, folder.getTitle(context))
            setTextColor(R.id.tv_folder_title, color)
            setTextColor(R.id.tv_folder_notes_count, color)
            setImageViewResource(R.id.iv_folder_icon, iconResource)
            setInt(R.id.iv_folder_icon, SetColorFilterMethodName, color)
            setOnClickFillInIntent(R.id.ll, intent)
            setViewVisibility(R.id.tv_folder_notes_count, if (isShowNotesCount) View.VISIBLE else View.GONE)
            widgetRadius?.toWidgetShapeId()?.let { widgetRadius ->
                setInt(R.id.ll, SetBackgroundResourceMethodName, widgetRadius)
            }
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = folders[position].first.id

    override fun hasStableIds(): Boolean = true
}