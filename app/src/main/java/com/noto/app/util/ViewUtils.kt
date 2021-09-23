package com.noto.app.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.noto.app.R
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

val Uri.directoryPath
    get() = path?.substringAfterLast(':')?.substringBeforeLast('/')

fun Fragment.launchShareNoteIntent(note: Note) {
    val intent = note.createShareIntent()
    val chooser = Intent.createChooser(intent, resources.stringResource(R.string.share_note))
    startActivity(chooser)
}

private fun Note.createShareIntent() = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, format())
}

fun View.snackbar(message: String, anchorView: View? = null) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    setBackgroundTint(resources.colorResource(R.color.colorPrimary))
    setTextColor(resources.colorResource(R.color.colorBackground))
    setAnchorView(anchorView)
    show()
}

fun View.setFullSpan() {
    if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams)
        (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
}

fun GradientDrawable.toRippleDrawable(resources: Resources): RippleDrawable {
    return RippleDrawable(resources.colorStateResource(R.color.colorSecondary)!!, this, this)
}

fun Activity.showKeyboard(view: View) = WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard(view: View) = WindowInsetsControllerCompat(window, view).hide(WindowInsetsCompat.Type.ime())
fun View.showKeyboardUsingImm() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun TextView.setBoldFont(font: Font) {
    when (font) {
        Font.Nunito -> context?.tryLoadingFontResource(R.font.nunito_bold)?.let { typeface = it }
        Font.Monospace -> setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
    }
}

fun TextView.setSemiboldFont(font: Font) {
    when (font) {
        Font.Nunito -> context?.tryLoadingFontResource(R.font.nunito_semibold)?.let { typeface = it }
        Font.Monospace -> typeface = Typeface.MONOSPACE
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun EditText.textAsFlow(emitNewTextOnly: Boolean = false): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, start, before, count ->
            if (emitNewTextOnly) {
                if (before <= count)
                    trySend(text)
            } else {
                trySend(text)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}