package com.noto.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.noto.app.R
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.math.absoluteValue

const val SetColorFilterMethodName = "setColorFilter"
const val SetBackgroundResourceMethodName = "setBackgroundResource"
const val SwipeGestureThreshold = 100F
const val DebounceTimeoutMillis = 250L
const val DisabledAlpha = 127
const val EnabledAlpha = 255

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
    @StringRes stringId: Int,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: NotoColor? = null,
    vararg formatArgs: Any? = emptyArray(),
) = Snackbar.make(this, context.stringResource(stringId, *formatArgs), Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    if (anchorViewId != null) setAnchorView(anchorViewId)
    if (drawableId != null) {
        textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
        textView?.compoundDrawablePadding = context.dimenResource(R.dimen.spacing_normal).toInt()
        textView?.gravity = Gravity.CENTER
    }
    if (color != null) {
        val backgroundColor = context.colorResource(color.toResource())
        val contentColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    } else {
        val backgroundColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
        val contentColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
        setBackgroundTint(backgroundColor)
        setTextColor(contentColor)
        textView?.compoundDrawablesRelative?.get(0)?.mutate()?.setTint(contentColor)
    }
    show()
}

fun View.setFullSpan() {
    if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams)
        (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
}

fun GradientDrawable.toRippleDrawable(context: Context): RippleDrawable {
    val colorStateList = context.colorAttributeResource(R.attr.notoSecondaryColor).toColorStateList()
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

fun EditText.textAsFlow(emitInitialText: Boolean = false): Flow<CharSequence?> = callbackFlow {
    val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}.onStart { if (emitInitialText) emit(text) }

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
    thumbCheckedColor: Int = context.colorAttributeResource(R.attr.notoPrimaryColor),
    thumbUnCheckedColor: Int = context.colorAttributeResource(R.attr.notoSurfaceColor),
    trackCheckedColor: Int = context.colorAttributeResource(R.attr.notoPrimaryColor),
    trackUnCheckedColor: Int = context.colorAttributeResource(R.attr.notoSecondaryColor),
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
    val backgroundColorStateList = colorAttributeResource(R.attr.notoBackgroundColor).toColorStateList()
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
    val backgroundColorStateList = colorAttributeResource(R.attr.notoSecondaryColor).toColorStateList()
    val gradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        this.color = colorStateList
        cornerRadius = dimenResource(R.dimen.spacing_small)
    }
    return RippleDrawable(backgroundColorStateList, gradientDrawable, null)
}

fun View.isLayoutVisible(rootView: View): Boolean {
    val rect = Rect()
    rootView.getHitRect(rect)
    return getLocalVisibleRect(rect)
}

fun TextView.getDisplayedTextIndex(scrollPosition: Int): Int {
    val lineNumber = layout.getLineForVertical(scrollPosition)
    val start: Int = layout.getLineStart(lineNumber)
    val displayed: String = text.toString().substring(start)
    return text.toString().indexOf(displayed)
}

fun View.scrollPositionAsFlow() = callbackFlow {
    val listener = ViewTreeObserver.OnScrollChangedListener { trySend(Unit) }
    viewTreeObserver?.addOnScrollChangedListener(listener)
    awaitClose { viewTreeObserver?.removeOnScrollChangedListener(listener) }
}

// Works only on API 29 and above
//fun View.keyboardVisibilityAsFlow() = callbackFlow {
//    val listener = OnApplyWindowInsetsListener { _, insets ->
//        val isVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
//        trySend(isVisible)
//        insets
//    }
//    ViewCompat.setOnApplyWindowInsetsListener(this@keyboardVisibilityAsFlow, listener)
//    awaitClose { ViewCompat.setOnApplyWindowInsetsListener(this@keyboardVisibilityAsFlow, null) }
//}

fun View.keyboardVisibilityAsFlow() = onPreDrawFlow()
    .map { isKeyboardVisible() }
    .distinctUntilChanged()

fun View.onPreDrawFlow() = callbackFlow {
    val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        trySend(Unit)
        true
    }
    viewTreeObserver?.addOnPreDrawListener(onPreDrawListener)
    awaitClose { viewTreeObserver?.removeOnPreDrawListener(onPreDrawListener) }
}

fun View.isKeyboardVisible(): Boolean = ViewCompat.getRootWindowInsets(this)
    ?.isVisible(WindowInsetsCompat.Type.ime()) ?: false

fun View.isFocusedAsFlow() = callbackFlow {
    setOnFocusChangeListener { _, hasFocus -> trySend(hasFocus) }
    awaitClose { onFocusChangeListener = null }
}.onStart { emit(isFocused) }

fun View.disable() {
    alpha = 0.5F
    isEnabled = false
}

fun View.enable() {
    alpha = 1F
    isEnabled = true
}

@SuppressLint("ClickableViewAccessibility")
inline fun View.setOnSwipeGestureListener(
    crossinline onSwipeLeft: () -> Unit,
    crossinline onSwipeRight: () -> Unit,
    threshold: Float = SwipeGestureThreshold,
) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            val x1 = e1?.x ?: 0F
            val x2 = e2?.x ?: 0F
            val diffX = x2 - x1
            return if (diffX.absoluteValue > threshold) {
                if (x2 > x1) onSwipeRight() else onSwipeLeft()
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

fun RecyclerView.isScrollingAsFlow() = callbackFlow {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            trySend(canScrollVertically(-1))
        }
    }
    addOnScrollListener(listener)
    awaitClose { removeOnScrollListener(listener) }
}

fun NestedScrollView.isScrollingAsFlow() = callbackFlow {
    val listener = NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
        trySend(canScrollVertically(-1))
    }
    setOnScrollChangeListener(listener)
    awaitClose { setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?) }
}