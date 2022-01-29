package com.noto.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.noto.app.R
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlin.math.absoluteValue

const val SetColorFilterMethodName = "setColorFilter"
const val SetBackgroundResourceMethodName = "setBackgroundResource"
const val SwipeGestureThreshold = 100F

fun NavController.navigateSafely(directions: NavDirections, builder: (NavOptionsBuilder.() -> Unit)? = null) {
    if (currentDestination?.getAction(directions.actionId) != null)
        if (builder == null)
            navigate(directions)
        else
            navigate(directions, navOptions(builder))
}

val Fragment.navController: NavController?
    get() = if (isAdded) findNavController() else null

val NavController.lastDestinationId: Long?
    @SuppressLint("RestrictedApi")
    get() {
        val lastBackStackEntry = backStack.lastOrNull {
            it.destination.id == R.id.folderFragment || it.destination.id == R.id.allNotesFragment || it.destination.id == R.id.recentNotesFragment
        }
        return when (lastBackStackEntry?.destination?.id) {
            R.id.allNotesFragment -> AllNotesItemId
            R.id.recentNotesFragment -> RecentNotesItemId
            else -> lastBackStackEntry?.arguments?.getLong(Constants.FolderId)
        }
    }

val Uri.directoryPath
    get() = path?.substringAfterLast(':')

fun Fragment.launchShareNoteIntent(note: Note) {
    val intent = note.createShareIntent()
    val chooser = Intent.createChooser(intent, context?.stringResource(R.string.share_note))
    startActivity(chooser)
}

private fun Note.createShareIntent() = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, format())
}

fun View.snackbar(
    message: String,
    folder: Folder? = null,
) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    if (folder == null) {
        setBackgroundTint(context.attributeColoResource(R.attr.notoPrimaryColor))
        setTextColor(context.attributeColoResource(R.attr.notoBackgroundColor))
    } else {
        setBackgroundTint(context.colorResource(folder.color.toResource()))
        setTextColor(context.attributeColoResource(R.attr.notoBackgroundColor))
    }
    val params = view.layoutParams as? CoordinatorLayout.LayoutParams
    params?.let {
        it.gravity = Gravity.TOP
        view.layoutParams = it
    }
    show()
}

fun View.setFullSpan() {
    if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams)
        (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
}

fun GradientDrawable.toRippleDrawable(context: Context): RippleDrawable {
    val colorStateList = context.attributeColoResource(R.attr.notoSecondaryColor).toColorStateList()
    return RippleDrawable(colorStateList, this, this)
}

fun Activity.showKeyboard(view: View) = WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard(view: View) = WindowInsetsControllerCompat(window, view).hide(WindowInsetsCompat.Type.ime())
fun View.showKeyboardUsingImm() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun TextView.setBoldFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_bold)?.let { typeface = it }
        Font.Monospace -> setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
    }
}

fun TextView.setSemiboldFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_semibold)?.let { typeface = it }
        Font.Monospace -> typeface = Typeface.MONOSPACE
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun EditText.textAsFlow(emitNewTextOnly: Boolean = false): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, start, before, count ->
//            if (emitNewTextOnly) {
//                if (before <= count)
//                    trySend(text)
//            } else {
                trySend(text)
//            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (urlSpan in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(
            object : URLSpan(urlSpan.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            },
            spannable.getSpanStart(urlSpan),
            spannable.getSpanEnd(urlSpan),
            0,
        )
    }
    text = spannable
}

fun SwitchMaterial.setupColors(
    thumbCheckedColor: Int = context.attributeColoResource(R.attr.notoPrimaryColor),
    thumbUnCheckedColor: Int = context.attributeColoResource(R.attr.notoSurfaceColor),
    trackCheckedColor: Int = context.attributeColoResource(R.attr.notoPrimaryColor),
    trackUnCheckedColor: Int = context.attributeColoResource(R.attr.notoSecondaryColor),
) {
    val state = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
    val thumbColors = intArrayOf(thumbCheckedColor, thumbUnCheckedColor)
    val trackColors = intArrayOf(
        ColorUtils.setAlphaComponent(trackCheckedColor, 128),
        ColorUtils.setAlphaComponent(trackUnCheckedColor, 128)
    )
    thumbTintList = ColorStateList(state, thumbColors)
    trackTintList = ColorStateList(state, trackColors)
}

fun BottomAppBar.setRoundedCorners() {
    val babBackgroundDrawable = background as MaterialShapeDrawable
    babBackgroundDrawable.shapeAppearanceModel = babBackgroundDrawable.shapeAppearanceModel
        .toBuilder()
        .setAllCorners(RoundedCornerTreatment())
        .setAllCornerSizes(RelativeCornerSize(0.5F))
        .build()
}

fun @receiver:ColorInt Int.withDefaultAlpha(alpha: Int = 32): Int = ColorUtils.setAlphaComponent(this, alpha)

@SuppressLint("ClickableViewAccessibility")
inline fun BottomAppBar.setOnSwipeGestureListener(crossinline callback: () -> Unit) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val diffY = (e2?.y ?: 0F) - (e1?.y ?: 0F)
            return if (diffY.absoluteValue > SwipeGestureThreshold) {
                callback()
                true
            } else {
                false
            }
        }
    }

    val gestureDetector = GestureDetector(context, gestureListener)

    setOnTouchListener { _, event ->
        gestureDetector.onTouchEvent(event)
        false
    }
}

fun Drawable.setRippleColor(colorStateList: ColorStateList) {
    val rippleDrawable = mutate() as RippleDrawable
    rippleDrawable.setColor(colorStateList.withAlpha(32))
}

fun RecyclerView.resetAdapter() {
    adapter = adapter
}

val AppBarLayout.isExpanded
    get() = (height - bottom) == 0

fun Context.createDialogItemStateListDrawable(notoColor: NotoColor): StateListDrawable {
    val checkedDrawable = createCheckedDrawable(notoColor)
    val uncheckedDrawable = createUncheckedDrawable(notoColor)
    val stateDrawable = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_checked), checkedDrawable)
        addState(intArrayOf(-android.R.attr.state_checked), uncheckedDrawable)
    }
    return stateDrawable
}

private fun Context.createUncheckedDrawable(notoColor: NotoColor): RippleDrawable {
    val color = colorResource(notoColor.toResource())
    val colorStateList = color.withDefaultAlpha().toColorStateList()
    val backgroundColorStateList = attributeColoResource(R.attr.notoBackgroundColor).toColorStateList()
    val gradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        this.color = backgroundColorStateList
        cornerRadius = dimenResource(R.dimen.spacing_small)
    }
    return RippleDrawable(colorStateList, gradientDrawable, null)
}

private fun Context.createCheckedDrawable(notoColor: NotoColor): RippleDrawable {
    val color = colorResource(notoColor.toResource())
    val colorStateList = color.withDefaultAlpha().toColorStateList()
    val backgroundColorStateList = attributeColoResource(R.attr.notoSecondaryColor).toColorStateList()
    val gradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        this.color = colorStateList
        cornerRadius = dimenResource(R.dimen.spacing_small)
    }
    return RippleDrawable(backgroundColorStateList, gradientDrawable, null)
}