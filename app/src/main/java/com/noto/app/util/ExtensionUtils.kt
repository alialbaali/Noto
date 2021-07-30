package com.noto.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.noto.app.R
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingMethod

enum class LayoutManager {
    Linear, Grid
}

inline fun <T> Iterable<T>.sortByMethod(method: SortingMethod, crossinline selector: (T) -> Comparable<*>?): Iterable<T> {
    return when (method) {
        SortingMethod.Asc -> sortedWith(compareBy(selector))
        SortingMethod.Desc -> sortedWith(compareByDescending(selector))
    }
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
    setBackgroundTint(colorResource(R.color.colorPrimary))
    setTextColor(colorResource(R.color.colorBackground))
    setAnchorView(anchorView)
    show()
}

fun View.toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT)

fun CollapsingToolbarLayout.setFontFamily() {
    setCollapsedTitleTypeface(fontResource(R.font.arima_madurai_bold))
    setExpandedTitleTypeface(fontResource(R.font.arima_madurai_medium))
}

fun AlarmManager.setAlarm(type: Int, timeInMills: Long, pendingIntent: PendingIntent) = AlarmManagerCompat.setExactAndAllowWhileIdle(this, type, timeInMills, pendingIntent)

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