package com.noto.app.util

import android.content.res.Resources
import com.airbnb.epoxy.EpoxyController
import com.noto.app.R
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note

inline fun EpoxyController.buildNotesModels(
    library: Library,
    notes: List<Pair<Note, List<Label>>>,
    resources: Resources,
    items: (List<Pair<Note, List<Label>>>) -> Unit,
) {
    when (library.grouping) {
        Grouping.Default -> {
            val pinnedNotes = notes.filter { it.first.isPinned }.sorted(library.sorting, library.sortingOrder)
            val notPinnedNotes = notes.filterNot { it.first.isPinned }.sorted(library.sorting, library.sortingOrder)

            if (pinnedNotes.isNotEmpty()) {
                headerItem {
                    id("pinned")
                    title(resources.stringResource(R.string.pinned))
                }

                items(pinnedNotes)

                if (notPinnedNotes.isNotEmpty())
                    headerItem {
                        id("notes")
                        title(resources.stringResource(R.string.notes))
                    }
            }
            items(notPinnedNotes)
        }
        Grouping.CreationDate -> {
            notes.groupByDate(library.sorting, library.sortingOrder).forEach { (date, notes) ->
                headerItem {
                    id(date.dayOfYear)
                    title(date.format())
                }
                items(notes)
            }
        }
        Grouping.Label -> {
            notes.groupByLabels(library.sorting, library.sortingOrder).forEach { (labels, notes) ->
                if (labels.isEmpty())
                    headerItem {
                        id("without_label")
                        title(resources.stringResource(R.string.without_label))
                    }
                else
                    headerItem {
                        id(*labels.map { it.id }.toTypedArray())
                        title(labels.joinToString(" • ") { it.title })
                    }
                items(notes)
            }
        }
    }
}