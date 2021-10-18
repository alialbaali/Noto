package com.noto.app.util

import android.view.View
import androidx.viewbinding.ViewBinding
import com.noto.app.domain.model.*

val LabelDefaultStrokeWidth = 3.dp
val LabelDefaultCornerRadius = 1000.dp.toFloat()
const val DefaultAnimationDuration = 250L

inline fun <T> List<T>.sortByOrder(sortingOrder: SortingOrder, crossinline selector: (T) -> Comparable<*>?): List<T> = when (sortingOrder) {
    SortingOrder.Ascending -> sortedWith(compareBy(selector))
    SortingOrder.Descending -> sortedWith(compareByDescending(selector))
}

fun String?.firstLineOrEmpty() = this?.lines()?.firstOrNull()?.trim() ?: ""

fun String?.takeAfterFirstLineOrEmpty() = this?.lines()?.drop(1)?.joinToString("\n")?.trim() ?: ""

fun String.takeLines(n: Int) = lines().take(n).joinToString("\n")

val Note.wordsCount
    get() = if (body.isBlank()) 0 else body.split("\\s+".toRegex()).size

inline fun <T : ViewBinding> T.withBinding(crossinline block: T.() -> Unit): View {
    block()
    return root
}

fun Note.format(): String = """
    $title
    
    $body
""".trimIndent()
    .trim()

val Note.isValid
    get() = title.isNotBlank() || body.isNotBlank()

fun List<Library>.sorted(sortingType: LibraryListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { library ->
    when (sortingType) {
        LibraryListSortingType.Manual -> library.position
        LibraryListSortingType.CreationDate -> library.creationDate
        LibraryListSortingType.Alphabetical -> library.title
    }
}

fun List<Pair<Note, List<Label>>>.sorted(sortingType: NoteListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { pair ->
    when (sortingType) {
        NoteListSortingType.Manual -> pair.first.position
        NoteListSortingType.CreationDate -> pair.first.creationDate
        NoteListSortingType.Alphabetical -> pair.first.title.ifBlank { pair.first.body }
    }
}

fun List<Pair<Note, List<Label>>>.filterSelectedLabels(labels: Map<Label, Boolean>) = filter { pair ->
    val selectedLabels = labels.entries
        .filter { it.value }
        .map { it.key }
    pair.second.containsAll(selectedLabels)
}

fun List<Pair<Note, List<Label>>>.groupByDate(sortingType: NoteListSortingType, sortingOrder: SortingOrder) =
    groupBy { it.first.creationDate.toLocalDate() }
        .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { it.first.isPinned } }
        .map { it.toPair() }
        .sortedByDescending { it.first }

fun List<Pair<Note, List<Label>>>.groupByLabels(sortingType: NoteListSortingType, sortingOrder: SortingOrder) =
    map { it.second to (it.first to emptyList<Label>()) }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { it.first.isPinned } }
        .map { it.toPair() }
        .sortedBy { it.first.firstOrNull()?.position }