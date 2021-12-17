package com.noto.app.util

import android.content.Context
import android.content.res.Resources
import com.airbnb.epoxy.EpoxyController
import com.noto.app.R
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note

inline fun EpoxyController.buildNotesModels(
    context: Context,
    library: Library,
    notes: List<NoteWithLabels>,
    items: (List<NoteWithLabels>) -> Unit,
) {
    when (library.grouping) {
        Grouping.Default -> {
            val pinnedNotes = notes.filter { it.first.isPinned }.sorted(library.sortingType, library.sortingOrder)
            val notPinnedNotes = notes.filterNot { it.first.isPinned }.sorted(library.sortingType, library.sortingOrder)

            if (pinnedNotes.isNotEmpty()) {
                headerItem {
                    id("pinned")
                    title(context.stringResource(R.string.pinned))
                }

                items(pinnedNotes)

                if (notPinnedNotes.isNotEmpty())
                    headerItem {
                        id("notes")
                        title(context.stringResource(R.string.notes))
                    }
            }
            items(notPinnedNotes)
        }
        Grouping.CreationDate -> {
            notes.groupByDate(library.sortingType, library.sortingOrder).forEach { (date, notes) ->
                headerItem {
                    id(date.dayOfYear)
                    title(date.format())
                }
                items(notes)
            }
        }
        Grouping.Label -> {
            notes.groupByLabels(library.sortingType, library.sortingOrder).forEach { (labels, notes) ->
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
                items(notes)
            }
        }
    }
}