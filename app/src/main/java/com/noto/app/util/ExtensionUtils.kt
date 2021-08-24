package com.noto.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingOrder
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

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

val Uri.directoryPath
    get() = path?.substringAfterLast(':')?.substringBeforeLast('/')

fun Note.formatCreationDate(): String {
    val timeZone = TimeZone.currentSystemDefault()
    return creationDate
        .toLocalDateTime(timeZone)
        .toJavaLocalDateTime()
        .let {
            val currentDateTime = Clock.System
                .now()
                .toLocalDateTime(timeZone)
                .toJavaLocalDateTime()

            if (it.year > currentDateTime.year)
                it.format(DateTimeFormatter.ofPattern("EEE, MMM d yyyy"))
            else
                it.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        }
}

fun Fragment.launchShareNoteIntent(note: Note) {
    val intent = note.createShareIntent()
    val chooser = Intent.createChooser(intent, resources.stringResource(R.string.share_note))
    startActivity(chooser)
}

private fun Note.createShareIntent() = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, format())
}

inline fun <T : ViewBinding> T.withBinding(crossinline block: T.() -> Unit): View {
    block()
    return root
}

fun Note.format(): String = """
    $title
    
    $body
""".trimIndent()
    .trim()

fun Activity.showKeyboard(view: View) = WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard(view: View) = WindowInsetsControllerCompat(window, view).hide(WindowInsetsCompat.Type.ime())

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun View.snackbar(message: String, anchorView: View? = null) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    setBackgroundTint(resources.colorResource(R.color.colorPrimary))
    setTextColor(resources.colorResource(R.color.colorBackground))
    setAnchorView(anchorView)
    show()
}

fun Note.isValid(): Boolean = !(title.isBlank() && body.isBlank())

fun Int.toCountText(single: String, plural: String) = if (this == 1) "$this ${single.lowercase()}" else "$this ${plural.lowercase()}"

fun NotoColor.toResource(): Int = when (this) {
    NotoColor.Blue -> R.color.colorAccentBlue
    NotoColor.Gray -> R.color.colorAccentGray
    NotoColor.Pink -> R.color.colorAccentPink
    NotoColor.Cyan -> R.color.colorAccentCyan
    NotoColor.Purple -> R.color.colorAccentPurple
    NotoColor.Red -> R.color.colorAccentRed
    NotoColor.Yellow -> R.color.colorAccentYellow
    NotoColor.Orange -> R.color.colorAccentOrange
    NotoColor.Green -> R.color.colorAccentGreen
    NotoColor.Brown -> R.color.colorAccentBrown
    NotoColor.BlueGray -> R.color.colorAccentBlueGray
    NotoColor.Teal -> R.color.colorAccentTeal
}

fun <T> MutableList<T>.replaceWith(value: T, predicate: (T) -> Boolean) {
    val result = first(predicate)
    this[indexOf(result)] = value
}

fun Context.exportNote(uri: Uri, library: Library, note: Note): Uri? {
    val fileName = note.title.ifBlank { note.body }
    return DocumentFile.fromTreeUri(this, uri)
        ?.let { it.findFile("Noto") ?: it.createDirectory("Noto") }
        ?.let { it.findFile(library.title) ?: it.createDirectory(library.title) }
        ?.createFile("text/plain", fileName)
        ?.uri
        ?.also { documentUri ->
            val noteContent = note.format()
            contentResolver
                .openOutputStream(documentUri, "w")
                ?.use { it.write(noteContent.toByteArray()) }
        }
}

fun Context.createPinnedShortcut(library: Library): ShortcutInfoCompat {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT, null, this, AppActivity::class.java).apply {
        putExtra(Constants.LibraryId, library.id)
    }

    val resourceId = library.color.toResource()

    val bitmap = resources.drawableResource(R.mipmap.ic_create_note)
        ?.toBitmap()
        ?.applyCanvas {
            drawColor(resources.colorResource(android.R.color.white))
            resources.drawableResource(R.drawable.ic_round_edit_24)?.mutate()?.let { drawable ->
                val spacing = 32
                drawable.setTint(resources.colorResource(resourceId))
                drawable.setBounds(spacing, spacing, width - spacing, height - spacing)
                drawable.draw(this)
            }
        }

    return ShortcutInfoCompat.Builder(this, library.id.toString())
        .setIntent(intent)
        .setShortLabel(library.title)
        .setLongLabel(library.title)
        .setIcon(IconCompat.createWithBitmap(bitmap))
        .build()
}