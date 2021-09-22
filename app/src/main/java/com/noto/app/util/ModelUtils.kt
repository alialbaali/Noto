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

fun Library.getArchiveText(archiveText: String) = "$title ${archiveText.lowercase()}"

fun Note.countWords(single: String, plural: String) = if (body.isBlank())
    "0 $plural".lowercase()
else body.split("\\s+".toRegex())
    .size
    .toCountText(single, plural)

inline fun <T : ViewBinding> T.withBinding(crossinline block: T.() -> Unit): View {
    block()
    return root
}

fun Note.format(): String = """
    $title
    
    $body
""".trimIndent()
    .trim()

fun Note.isValid(): Boolean = !(title.isBlank() && body.isBlank())

fun Int.toCountText(single: String, plural: String) = if (this == 1) "$this ${single.lowercase()}" else "$this ${plural.lowercase()}"

fun List<Library>.sorted(sorting: LibraryListSorting, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { library ->
    when (sorting) {
        LibraryListSorting.Manual -> library.position
        LibraryListSorting.CreationDate -> library.creationDate
        LibraryListSorting.Alphabetical -> library.title
    }
}

fun List<Pair<Note, List<Label>>>.sorted(sorting: NoteListSorting, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { pair ->
    when (sorting) {
        NoteListSorting.Manual -> pair.first.position
        NoteListSorting.CreationDate -> pair.first.creationDate
        NoteListSorting.Alphabetical -> pair.first.title.ifBlank { pair.first.body }
    }
}

fun List<Pair<Note, List<Label>>>.filterSelectedLabels(labels: Map<Label, Boolean>) = filter { pair ->
    val selectedLabels = labels.entries
        .filter { it.value }
        .map { it.key }
    pair.second.containsAll(selectedLabels)
}