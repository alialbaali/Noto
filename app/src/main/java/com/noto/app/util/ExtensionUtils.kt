package com.noto.app.util

import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.noto.app.R
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingMethod
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

enum class LayoutManager {
    Linear, Grid
}

inline fun <T> Iterable<T>.sortByMethod(method: SortingMethod, crossinline selector: (T) -> Comparable<*>?): Iterable<T> {
    return when (method) {
        SortingMethod.Asc -> sortedWith(compareBy(selector))
        SortingMethod.Desc -> sortedWith(compareByDescending(selector))
    }
}

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

fun InputMethodManager.showKeyboard() = toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
fun InputMethodManager.hideKeyboard(windowToken: IBinder) = hideSoftInputFromWindow(windowToken, 0)

fun Note.format(): String = """
    $title
    
    $body
""".trimIndent()

//fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)?.show(WindowInsetsCompat.Type.ime())
//fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun View.snackbar(message: String, anchorView: View? = null) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    setBackgroundTint(resources.colorResource(R.color.colorPrimary))
    setTextColor(resources.colorResource(R.color.colorBackground))
    setAnchorView(anchorView)
    show()
}

fun Note.isValid(): Boolean = !(title.isBlank() && body.isBlank())

fun NotoColor.toResource(): Int = when (this) {
    NotoColor.BLUE -> R.color.colorAccentBlue
    NotoColor.GRAY -> R.color.colorAccentGray
    NotoColor.PINK -> R.color.colorAccentPink
    NotoColor.CYAN -> R.color.colorAccentCyan
    NotoColor.PURPLE -> R.color.colorAccentPurple
    NotoColor.RED -> R.color.colorAccentRed
    NotoColor.YELLOW -> R.color.colorAccentYellow
    NotoColor.ORANGE -> R.color.colorAccentOrange
    NotoColor.GREEN -> R.color.colorAccentGreen
    NotoColor.BROWN -> R.color.colorAccentBrown
    NotoColor.BLUE_GRAY -> R.color.colorAccentBlueGray
    NotoColor.TEAL -> R.color.colorAccentTeal
}

fun <T> MutableList<T>.replaceWith(value: T, predicate: (T) -> Boolean) {
    val result = first(predicate)
    this[indexOf(result)] = value
}