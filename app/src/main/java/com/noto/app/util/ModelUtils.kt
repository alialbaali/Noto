package com.noto.app.util

import android.content.Context
import android.util.Base64
import android.view.View
import androidx.viewbinding.ViewBinding
import com.noto.app.R
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.folder.NoteItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.time.Duration.Companion.days

const val AllNotesItemId = -2L
const val RecentNotesItemId = -3L
const val AllFoldersId = -4L
val LabelDefaultStrokeWidth = 2.dp

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
    get() = if (isBlank()) 0 else split("\\s+".toRegex()).filter { it.isNotBlank() }.size

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

fun List<NoteItemModel>.sorted(sortingType: NoteListSortingType, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { model ->
    when (sortingType) {
        NoteListSortingType.Manual -> model.note.position
        NoteListSortingType.CreationDate -> model.note.creationDate
        NoteListSortingType.Alphabetical -> model.note.title.ifBlank { model.note.body }
    }
}

fun List<NoteItemModel>.filterSelectedLabels(selectedLabels: List<Label>, filteringType: FilteringType) = filter { model ->
    if (selectedLabels.isNotEmpty()) {
        when (filteringType) {
            FilteringType.Inclusive -> model.labels.any { label -> selectedLabels.any { it == label } }
            FilteringType.Exclusive -> model.labels.containsAll(selectedLabels)
            FilteringType.Strict -> model.labels == selectedLabels
        }
    } else {
        true
    }
}

fun List<NoteItemModel>.filterContent(content: CharSequence) = filter { model ->
    model.note.title.contains(content, ignoreCase = true) || model.note.body.contains(content, ignoreCase = true)
}

fun List<NoteItemModel>.groupByDate(
    sortingType: NoteListSortingType,
    sortingOrder: SortingOrder,
    groupingOrder: GroupingOrder,
): List<Pair<LocalDate, List<NoteItemModel>>> = groupBy { model -> model.note.creationDate.toLocalDate() }
    .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { model -> model.note.isPinned } }
    .map { it.toPair() }
    .let {
        if (groupingOrder == GroupingOrder.Descending)
            it.sortedByDescending { it.first }
        else
            it.sortedBy { it.first }
    }

fun List<NoteItemModel>.groupByLabels(
    sortingType: NoteListSortingType,
    sortingOrder: SortingOrder,
    groupingOrder: GroupingOrder,
): List<Pair<List<Label>, List<NoteItemModel>>> = map { model -> model.labels to model.copy(labels = emptyList()) }
    .groupBy({ it.first }, { it.second })
    .mapValues { it.value.sorted(sortingType, sortingOrder).sortedByDescending { it.note.isPinned } }
    .map { it.toPair() }
    .let {
        if (groupingOrder == GroupingOrder.Descending)
            it.sortedByDescending { it.first.firstOrNull()?.position }
        else
            it.sortedBy { it.first.firstOrNull()?.position }
    }

fun List<Note>.mapToNoteItemModel(labels: List<Label>, noteLabels: List<NoteLabel>, selectedNoteIds: LongArray = longArrayOf()): List<NoteItemModel> {
    return map { note ->
        NoteItemModel(
            note,
            labels.sortedBy { it.position }
                .filter { label ->
                    noteLabels.filter { it.noteId == note.id }.any { noteLabel ->
                        noteLabel.labelId == label.id
                    }
                },
            isSelected = selectedNoteIds.contains(note.id),
        )
    }
}

fun List<Note>.filterRecentlyAccessed() = filter { it.accessDate >= Clock.System.now().minus(7.days) }

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

fun Flow<CharSequence?>.asSearchFlow() = filterNotNull()
    .map { it.trim().toString() }


fun Icon.toActivityAliasName(isAppActivityIconEnabled: Boolean) = when (this) {
    Icon.Futuristic -> if (isAppActivityIconEnabled) "AppActivity" else "Futuristic"
    Icon.DarkRain -> "DarkRain"
    Icon.Airplane -> "Airplane"
    Icon.BlossomIce -> "BlossomIce"
    Icon.DarkAlpine -> "DarkAlpine"
    Icon.DarkSide -> "DarkSide"
    Icon.Earth -> "Earth"
    Icon.Fire -> "Fire"
    Icon.Purpleberry -> "Purpleberry"
    Icon.SanguineSun -> "SanguineSun"
}.let { "com.noto.app.$it" }

suspend fun LabelRepository.getOrCreateLabel(folderId: Long, label: Label): Long {
    val folderLabels = getLabelsByFolderId(folderId).first()
    val existingLabel = folderLabels.firstOrNull { it.title == label.title }?.id
    return existingLabel ?: createLabel(label.copy(id = 0, folderId = folderId))
}