package com.noto.app.util

import android.util.Base64
import android.view.View
import androidx.viewbinding.ViewBinding
import com.noto.app.domain.model.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

val LabelDefaultStrokeWidth = 3.dp
val LabelDefaultCornerRadius = 1000.dp.toFloat()
const val DefaultAnimationDuration = 250L
typealias NoteWithLabels = Pair<Note, List<Label>>

private const val HashAlgorithm = "PBKDF2WithHmacSHA1"
private const val HashIterationCount = 65536
private const val HashKeyLength = 128

inline fun <T> List<T>.sortByOrder(sortingOrder: SortingOrder, crossinline selector: (T) -> Comparable<*>?): List<T> = when (sortingOrder) {
    SortingOrder.Ascending -> sortedWith(compareBy(selector))
    SortingOrder.Descending -> sortedWith(compareByDescending(selector))
}

fun String?.firstLineOrEmpty() = this?.lines()?.firstOrNull()?.trim().orEmpty()

fun String?.takeAfterFirstLineOrEmpty() = this?.lines()?.drop(1)?.joinToString("\n")?.trim().orEmpty()

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

fun List<Pair<Library, Int>>.sorted(sortingType: LibraryListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { pair ->
    when (sortingType) {
        LibraryListSortingType.Manual -> pair.first.position
        LibraryListSortingType.CreationDate -> pair.first.creationDate
        LibraryListSortingType.Alphabetical -> pair.first.title
    }
}

fun List<NoteWithLabels>.sorted(sortingType: NoteListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { pair ->
    when (sortingType) {
        NoteListSortingType.Manual -> pair.first.position
        NoteListSortingType.CreationDate -> pair.first.creationDate
        NoteListSortingType.Alphabetical -> pair.first.title.ifBlank { pair.first.body }
    }
}

fun List<NoteWithLabels>.filterSelectedLabels(labels: Map<Label, Boolean>) = filter { pair ->
    val selectedLabels = labels.entries
        .filter { it.value }
        .map { it.key }
    pair.second.containsAll(selectedLabels)
}

fun List<NoteWithLabels>.filterContent(content: CharSequence) = filter { entry ->
    entry.first.title.contains(content, ignoreCase = true) || entry.first.body.contains(content, ignoreCase = true)
}

fun List<NoteWithLabels>.groupByDate(sortingType: NoteListSortingType, sortingOrder: SortingOrder) =
    groupBy { it.first.creationDate.toLocalDate() }
        .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { it.first.isPinned } }
        .map { it.toPair() }
        .sortedByDescending { it.first }

fun List<NoteWithLabels>.groupByLabels(sortingType: NoteListSortingType, sortingOrder: SortingOrder) =
    map { it.second to (it.first to emptyList<Label>()) }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { it.first.isPinned } }
        .map { it.toPair() }
        .sortedBy { it.first.firstOrNull()?.position }

fun List<Note>.mapWithLabels(labels: List<Label>, noteLabels: List<NoteLabel>): List<NoteWithLabels> {
    return map { note ->
        note to labels
            .sortedBy { it.position }
            .filter { label ->
                noteLabels.filter { it.noteId == note.id }.any { noteLabel ->
                    noteLabel.labelId == label.id
                }
            }
    }
}

fun Map<Label, Boolean>.filterSelected() = filterValues { it }.map { it.key }

fun String.toLongList() = split(", ").mapNotNull { it.toLongOrNull() }

fun String.hash(): String {
    val salt = ByteArray(16)
    val spec = PBEKeySpec(this.toCharArray(), salt, HashIterationCount, HashKeyLength)
    val factory = SecretKeyFactory.getInstance(HashAlgorithm)
    val bytes = factory.generateSecret(spec).encoded
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

val Library.isInbox
    get() = id == Library.InboxId