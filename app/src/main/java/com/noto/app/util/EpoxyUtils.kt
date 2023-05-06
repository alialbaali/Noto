package com.noto.app.util

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.noto.app.R
import com.noto.app.components.headerItem
import com.noto.app.components.progressIndicatorItem
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.NotoColor
import com.noto.app.folder.NoteItemModel

fun EpoxyRecyclerView.setupProgressIndicator(color: NotoColor? = null) {
    withModels {
        progressIndicatorItem {
            id("loading")
            color(color)
        }
    }
}

inline fun EpoxyController.buildNotesModels(
    context: Context,
    folder: Folder,
    notes: List<NoteItemModel>,
    crossinline onCreateClick: (List<Long>) -> Unit,
    content: (List<NoteItemModel>) -> Unit,
) {
    when (folder.grouping) {
        Grouping.None -> {
            val pinnedNotes = notes.filter { it.note.isPinned }.sortedWith(NoteItemModel.Comparator(folder.sortingOrder, folder.sortingType))
            val notPinnedNotes = notes.filterNot { it.note.isPinned }.sortedWith(NoteItemModel.Comparator(folder.sortingOrder, folder.sortingType))

            if (pinnedNotes.isNotEmpty()) {
                headerItem {
                    id("pinned")
                    title(context.stringResource(R.string.pinned))
                    color(folder.color)
                }

                content(pinnedNotes)

                if (notPinnedNotes.isNotEmpty())
                    headerItem {
                        id("notes")
                        title(context.stringResource(R.string.notes))
                        color(folder.color)
                    }
            }
            content(notPinnedNotes)
        }

        Grouping.CreationDate -> {
            notes.groupByCreationDate(folder.sortingType, folder.sortingOrder, folder.groupingOrder).forEach { (date, notes) ->
                headerItem {
                    id(date.dayOfYear)
                    title(date.format())
                    color(folder.color)
                }
                content(notes)
            }
        }

        Grouping.Label -> {
            notes.groupByLabels(folder.sortingType, folder.sortingOrder, folder.groupingOrder).forEach { (labels, notes) ->
                if (labels.isEmpty())
                    headerItem {
                        id("without_label")
                        title(context.stringResource(R.string.without_label))
                        color(folder.color)
                        onCreateClickListener { _ -> onCreateClick(emptyList()) }
                    }
                else
                    headerItem {
                        id(*labels.map { it.id }.toTypedArray())
                        title(labels.joinToString(" • ") { it.title })
                        color(folder.color)
                        onCreateClickListener { _ -> onCreateClick(labels.map { it.id }) }
                    }
                content(notes)
            }
        }

        Grouping.AccessDate -> {
            notes.groupByAccessDate(folder.sortingType, folder.sortingOrder, folder.groupingOrder).forEach { (date, notes) ->
                headerItem {
                    id(date.dayOfYear)
                    title(date.format())
                    color(folder.color)
                }
                content(notes)
            }
        }
    }
}

inline fun EpoxyController.buildFoldersModels(
    context: Context,
    folders: List<Pair<Folder, Int>>,
    content: (List<Pair<Folder, Int>>) -> Unit,
) {
    val pinnedFolders = folders.filter { it.first.isPinned }
    val notPinnedFolders = folders.filterNot { it.first.isPinned }

    if (pinnedFolders.isNotEmpty()) {
        headerItem {
            id("pinned")
            title(context.stringResource(R.string.pinned))
        }

        content(pinnedFolders)

        if (notPinnedFolders.isNotEmpty())
            headerItem {
                id("libraries")
                title(context.stringResource(R.string.folders))
            }
    }
    content(notPinnedFolders)
}