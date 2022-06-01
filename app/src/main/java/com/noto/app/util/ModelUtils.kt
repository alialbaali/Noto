package com.noto.app.util

import android.content.Context
import android.util.Base64
import android.view.View
import androidx.viewbinding.ViewBinding
import com.noto.app.R
import com.noto.app.domain.model.*
import kotlinx.datetime.Clock
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.time.Duration.Companion.days

const val AllNotesItemId = -2L
const val RecentNotesItemId = -3L
val LabelDefaultStrokeWidth = 2.dp
val LabelDefaultCornerRadius = 8.dp.toFloat()
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

val CharSequence.wordsCount
    get() = if (isBlank()) 0 else split("\\s+".toRegex()).size

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

@Suppress("DEPRECATION")
fun List<Pair<Folder, Int>>.sorted(sortingType: FolderListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { pair ->
    when (sortingType) {
        FolderListSortingType.Manual -> pair.first.position
        FolderListSortingType.CreationDate -> pair.first.creationDate
        FolderListSortingType.Alphabetical -> pair.first.title
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

fun List<Note>.filterRecentlyAccessed() = filter { it.accessDate != null && it.accessDate >= Clock.System.now().minus(7.days) }

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K?, V>.filterNotNullKeys() = filterKeys { it != null } as Map<K, V>

fun Map<Label, Boolean>.filterSelected() = filterValues { it }.map { it.key }

fun String.toLongList() = split(", ").mapNotNull { it.toLongOrNull() }

fun String.hash(): String {
    val salt = ByteArray(16)
    val spec = PBEKeySpec(this.toCharArray(), salt, HashIterationCount, HashKeyLength)
    val factory = SecretKeyFactory.getInstance(HashAlgorithm)
    val bytes = factory.generateSecret(spec).encoded
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

val Folder.isGeneral
    get() = id == Folder.GeneralFolderId

@Suppress("DEPRECATION")
fun Folder.getTitle(context: Context) = if (isGeneral) context.stringResource(R.string.general) else title

fun List<Pair<Folder, Int>>.forEachRecursively(depth: Int = 1, block: (Pair<Folder, Int>, depth: Int) -> Unit) {
    forEach { entry ->
        block(entry, depth)
        entry.first.folders.forEachRecursively(depth + 1, block)
    }
}

fun List<Pair<Folder, Int>>.countRecursively(): Int {
    var count = count()
    forEach { entry ->
        count += entry.first.folders.countRecursively()
    }
    return count
}

fun List<Pair<Folder, Int>>.filterRecursively(predicate: (Pair<Folder, Int>) -> Boolean): List<Pair<Folder, Int>> {
    return filter(predicate).map {
        it.first.copy(
            folders = it.first.folders.filterRecursively(predicate)
        ) to it.second
    }
}

fun List<Pair<Folder, Int>>.findRecursively(predicate: (Pair<Folder, Int>) -> Boolean): Pair<Folder, Int>? {
    val item: Pair<Folder, Int>? = firstOrNull(predicate)
    if (item != null)
        return item
    else
        forEach {
            val result = it.first.folders.findRecursively(predicate)
            if (result != null)
                return result
        }
    return null
}