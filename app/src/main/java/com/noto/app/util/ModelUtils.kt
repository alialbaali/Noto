package com.noto.app.util

import android.content.Context
import android.util.Base64
import android.view.View
import androidx.core.os.LocaleListCompat
import androidx.viewbinding.ViewBinding
import com.noto.app.R
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.folder.NoteItemModel
import com.noto.app.label.LabelItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import java.text.Collator
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.time.Duration.Companion.days

const val AllFoldersId = -4L
val LabelDefaultStrokeWidth = 2.dp
const val LineSeparator = "\n\n"
val SelectedLabelsComparator = compareByDescending<LabelItemModel> { it.isSelected }.thenBy { it.label.position }

private const val HashAlgorithm = "PBKDF2WithHmacSHA1"
private const val HashIterationCount = 65536
private const val HashKeyLength = 128

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

@Suppress("FunctionName")
fun NoteItemModel.Companion.Comparator(sortingOrder: SortingOrder, sortingType: NoteListSortingType): Comparator<NoteItemModel> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    val isCollatorEnabled = sortingType == NoteListSortingType.Alphabetical
    val selector: (NoteItemModel) -> Comparable<*> = { model ->
        when (sortingType) {
            NoteListSortingType.Manual -> model.note.position
            NoteListSortingType.CreationDate -> model.note.creationDate
            NoteListSortingType.Alphabetical -> model.note.title.ifBlank { model.note.body }
            NoteListSortingType.AccessDate -> model.note.accessDate
        }
    }
    return compareByDescending<NoteItemModel> { model -> model.note.isPinned }
        .let {
            when (sortingOrder) {
                SortingOrder.Ascending -> if (isCollatorEnabled) it.thenBy(collator, selector) else it.thenBy(selector)
                SortingOrder.Descending -> if (isCollatorEnabled) it.thenByDescending(collator, selector) else it.thenByDescending(selector)
            }
        }
}

@Suppress("FunctionName")
fun Note.Companion.Comparator(sortingOrder: SortingOrder, sortingType: NoteListSortingType): Comparator<Note> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    val isCollatorEnabled = sortingType == NoteListSortingType.Alphabetical
    val selector: (Note) -> Comparable<*> = { note ->
        when (sortingType) {
            NoteListSortingType.Manual -> note.position
            NoteListSortingType.CreationDate -> note.creationDate
            NoteListSortingType.Alphabetical -> note.title.ifBlank { note.body }
            NoteListSortingType.AccessDate -> note.accessDate
        }
    }
    return compareByDescending<Note> { note -> note.isPinned }
        .let {
            when (sortingOrder) {
                SortingOrder.Ascending -> if (isCollatorEnabled) it.thenBy(collator, selector) else it.thenBy(selector)
                SortingOrder.Descending -> if (isCollatorEnabled) it.thenByDescending(collator, selector) else it.thenByDescending(selector)
            }
        }
}

@Suppress("DEPRECATION", "FunctionName")
fun Folder.Companion.Comparator(sortingOrder: SortingOrder, sortingType: FolderListSortingType): Comparator<Pair<Folder, Int>> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    val isCollatorEnabled = sortingType == FolderListSortingType.Alphabetical
    val selector: (Pair<Folder, Int>) -> Comparable<*> = { pair ->
        when (sortingType) {
            FolderListSortingType.Manual -> pair.first.position
            FolderListSortingType.CreationDate -> pair.first.creationDate
            FolderListSortingType.Alphabetical -> pair.first.title
        }
    }
    return compareByDescending<Pair<Folder, Int>> { pair -> pair.first.isPinned }
        .let {
            when (sortingOrder) {
                SortingOrder.Ascending -> if (isCollatorEnabled) it.thenBy(collator, selector) else it.thenBy(selector)
                SortingOrder.Descending -> if (isCollatorEnabled) it.thenByDescending(collator, selector) else it.thenByDescending(selector)
            }
        }
}

fun List<NoteItemModel>.filterByLabels(selectedLabels: List<Label>, filteringType: FilteringType) = filter { model ->
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

fun List<NoteItemModel>.filterBySearchTerm(searchTerm: CharSequence) = filter { model ->
    model.note.title.contains(searchTerm, ignoreCase = true) || model.note.body.contains(searchTerm, ignoreCase = true)
}

fun List<NoteItemModel>.groupByCreationDate(
    sortingType: NoteListSortingType,
    sortingOrder: SortingOrder,
    groupingOrder: GroupingOrder,
): List<Pair<LocalDate, List<NoteItemModel>>> = groupBy { model -> model.note.creationDate.toLocalDate() }
    .sorted(sortingType, sortingOrder, groupingOrder)

fun List<NoteItemModel>.groupByAccessDate(
    sortingType: NoteListSortingType,
    sortingOrder: SortingOrder,
    groupingOrder: GroupingOrder,
): List<Pair<LocalDate, List<NoteItemModel>>> = groupBy { model -> model.note.accessDate.toLocalDate() }
    .sorted(sortingType, sortingOrder, groupingOrder)

private fun Map<LocalDate, List<NoteItemModel>>.sorted(
    sortingType: NoteListSortingType,
    sortingOrder: SortingOrder,
    groupingOrder: GroupingOrder,
): List<Pair<LocalDate, List<NoteItemModel>>> =
    mapValues { it.value.sortedWith(NoteItemModel.Comparator(sortingOrder, sortingType)).sortedByDescending { model -> model.note.isPinned } }
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
    .mapValues { it.value.sortedWith(NoteItemModel.Comparator(sortingOrder, sortingType)).sortedByDescending { it.note.isPinned } }
    .map { it.toPair() }
    .let {
        if (groupingOrder == GroupingOrder.Descending)
            it.sortedByDescending { it.first.firstOrNull()?.position }
        else
            it.sortedBy { it.first.firstOrNull()?.position }
    }

fun List<Note>.mapToNoteItemModel(
    labels: List<Label>,
    noteLabels: List<NoteLabel>,
    selectedNoteIds: LongArray = longArrayOf(),
    draggedNoteIds: LongArray = longArrayOf(),
): List<NoteItemModel> {
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
            isDragged = draggedNoteIds.contains(note.id),
        )
    }
}

val Note.isRecent
    get() = accessDate >= Clock.System.now().minus(7.days)

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K?, V>.filterNotNullKeys() = filterKeys { it != null } as Map<K, V>

fun Map<Label, Boolean>.filterSelected() = filterValues { it }.map { it.key }
fun List<LabelItemModel>.filterSelected() = filter { it.isSelected }.map { it.label }

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

fun Language.Companion.Comparator(context: Context): Comparator<Language> {
    val collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    return compareByDescending<Language> { it == Language.System }
        .thenBy { it in Language.Deprecated }
        .thenBy(collator) { it ->
            val localizedContext = context.localize(it)
            localizedContext.stringResource(it.toResource())
        }
}

fun Language.toLocale(): Locale = when (this) {
    Language.System -> Locale.getDefault()
    Language.English -> Locale("en")
    Language.Turkish -> Locale("tr")
    Language.Arabic -> Locale("ar")
    Language.Indonesian -> Locale("in")
    Language.Russian -> Locale("ru")
    Language.Tamil -> Locale("ta")
    Language.Spanish -> Locale("es")
    Language.French -> Locale("fr")
    Language.German -> Locale("de")
    Language.Italian -> Locale("it")
    Language.Czech -> Locale("cs")
    Language.Lithuanian -> Locale("lt")
    Language.SimplifiedChinese -> Locale("zh")
    Language.Portuguese -> Locale("pt")
}

fun List<Language>.toLocalListCompat(): LocaleListCompat {
    return try {
        val locales = this.map { it.toLocale() }.toTypedArray()
        LocaleListCompat.create(*locales)
    } catch (e: Exception) {
        LocaleListCompat.getEmptyLocaleList()
    }
}

fun LocaleListCompat.toLanguages(): List<Language> {
    return toLanguageTags().split(',').map { tag ->
        when {
            tag.startsWith("en", ignoreCase = true) -> Language.English
            tag.startsWith("tr", ignoreCase = true) -> Language.Turkish
            tag.startsWith("ar", ignoreCase = true) -> Language.Arabic
            tag.startsWith("in", ignoreCase = true) -> Language.Indonesian
            tag.startsWith("ru", ignoreCase = true) -> Language.Russian
            tag.startsWith("ta", ignoreCase = true) -> Language.Tamil
            tag.startsWith("es", ignoreCase = true) -> Language.Spanish
            tag.startsWith("fr", ignoreCase = true) -> Language.French
            tag.startsWith("de", ignoreCase = true) -> Language.German
            tag.startsWith("it", ignoreCase = true) -> Language.Italian
            tag.startsWith("cs", ignoreCase = true) -> Language.Czech
            tag.startsWith("lt", ignoreCase = true) -> Language.Lithuanian
            tag.startsWith("zh", ignoreCase = true) -> Language.SimplifiedChinese
            tag.startsWith("pt", ignoreCase = true) -> Language.Portuguese
            else -> Language.System
        }
    }
}

fun CharSequence.indicesOf(string: String, startIndex: Int = 0, ignoreCase: Boolean = false): List<IntRange> {
    val indices = mutableListOf<IntRange>()
    var index = this.indexOf(string, startIndex, ignoreCase)
    while (index >= 0) {
        indices += index..(index + string.length)
        index = this.indexOf(string, startIndex = index + 1, ignoreCase)
    }
    return indices
}