package com.noto.app.util

import android.content.Context
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.noto.app.R
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.NotoColor

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
    notes: List<NoteWithLabels>,
    content: (List<NoteWithLabels>) -> Unit,
) {
    when (folder.grouping) {
        Grouping.Default -> {
            val pinnedNotes = notes.filter { it.first.isPinned }.sorted(folder.sortingType, folder.sortingOrder)
            val notPinnedNotes = notes.filterNot { it.first.isPinned }.sorted(folder.sortingType, folder.sortingOrder)

            if (pinnedNotes.isNotEmpty()) {
                headerItem {
                    id("pinned")
                    title(context.stringResource(R.string.pinned))
                }

                content(pinnedNotes)

                if (notPinnedNotes.isNotEmpty())
                    headerItem {
                        id("notes")
                        title(context.stringResource(R.string.notes))
                    }
            }
            content(notPinnedNotes)
        }
        Grouping.CreationDate -> {
            notes.groupByDate(folder.sortingType, folder.sortingOrder).forEach { (date, notes) ->
                headerItem {
                    id(date.dayOfYear)
                    title(date.format())
                }
                content(notes)
            }
        }
        Grouping.Label -> {
            notes.groupByLabels(folder.sortingType, folder.sortingOrder).forEach { (labels, notes) ->
                if (labels.isEmpty())
                    headerItem {
                        id("without_label")
                        title(context.stringResource(R.string.without_label))
                    }
                else
                    headerItem {
                        id(*labels.map { it.id }.toTypedArray())
                        title(labels.joinToString(" • ") { it.title })
                    }
                content(notes)
            }
        }
    }
}

inline fun EpoxyController.buildLibrariesModels(
    context: Context,
    libraries: List<Pair<Folder, Int>>,
    content: (List<Pair<Folder, Int>>) -> Unit,
) {
    val pinnedLibraries = libraries.filter { it.first.isPinned }
    val notPinnedLibraries = libraries.filterNot { it.first.isPinned }

    if (pinnedLibraries.isNotEmpty()) {
        headerItem {
            id("pinned")
            title(context.stringResource(R.string.pinned))
        }

        content(pinnedLibraries)

        if (notPinnedLibraries.isNotEmpty())
            headerItem {
                id("libraries")
                title(context.stringResource(R.string.libraries))
            }
    }
    content(notPinnedLibraries)
}