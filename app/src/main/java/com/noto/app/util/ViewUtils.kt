package com.noto.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.graphics.ColorUtils
import androidx.core.os.ConfigurationCompat
import androidx.core.text.toSpannable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
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
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.noto.app.R
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.filtered.FilteredItemModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.math.absoluteValue

const val SetColorFilterMethodName = "setColorFilter"
const val SetBackgroundResourceMethodName = "setBackgroundResource"
const val SetImageResource = "setImageResource"
const val SwipeGestureThreshold = 100F
const val DebounceTimeoutMillis = 250L
const val DisabledAlpha = 127
const val EnabledAlpha = 255

fun NavController.navigateSafely(
    directions: NavDirections,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    if (currentDestination?.getAction(directions.actionId) != null)
        if (builder == null)
            navigate(directions)
        else
            navigate(directions, navOptions(builder))
}

val Fragment.navController: NavController?
    get() = if (isAdded) findNavController() else null

@Suppress("DEPRECATION")
val NavController.lastDestinationIdOrNull: Long?
    @SuppressLint("RestrictedApi")
    get() {
        val args = currentBackStack.value.lastOrNull {
            it.destination.id == R.id.folderFragment || it.destination.id == R.id.filteredFragment
        }?.arguments

        val folderId = args?.getLong(Constants.FolderId)?.takeUnless { it == 0L }
        val model = args?.get(Constants.Model) as? FilteredItemModel

        return folderId ?: model?.id
    }

val Uri.directoryPath
    get() = path?.substringAfterLast(':')

fun Fragment.launchShareNotesIntent(notes: List<Note>) {
    val notesText = notes.joinToString(LineSeparator) { it.format() }.trim()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, notesText)
    }
    val chooser = Intent.createChooser(
        intent,
        context?.quantityStringResource(R.plurals.share_note, notes.count(), notes.count())
    )
    startActivity(chooser)
}

fun View.snackbar(
    text: String,
    @DrawableRes drawableId: Int? = null,
    @IdRes anchorViewId: Int? = null,
    color: NotoColor? = null,
    vibrate: Boolean = true,
) = Snackbar.make(this, text, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    if (anchorViewId != null) setAnchorView(anchorViewId)
    if (drawableId != null) {
        textView?.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
        textView?.compoundDrawablePadding = context.dimenResource(R.dimen.spacing_normal).toInt()
        textView?.gravity = Gravity.CENTER
    }
    if (color != null) {
        val backgroundColor = context.colorResource(color.toColorResourceId())
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
    if (vibrate) performClickHapticFeedback()
    show()
}

fun View.setFullSpan() {
    if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams)
        (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
}

fun GradientDrawable.toRippleDrawable(context: Context): RippleDrawable {
    val colorStateList =
        context.colorAttributeResource(R.attr.notoSecondaryColor).toColorStateList()
    return RippleDrawable(colorStateList, this, this)
}

fun Activity.showKeyboard(view: View) =
    WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.ime())

fun Activity.hideKeyboard(view: View) =
    WindowInsetsControllerCompat(window, view).hide(WindowInsetsCompat.Type.ime())

fun View.showKeyboardUsingImm() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun TextView.setSemiboldFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_semibold)?.let { typeface = it }
        Font.Monospace -> setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
    }
}

fun TextView.setMediumFont(font: Font) {
    when (font) {
        Font.Nunito -> context.tryLoadingFontResource(R.font.nunito_medium)?.let { typeface = it }
        Font.Monospace -> typeface = Typeface.MONOSPACE
    }
}

fun CustomEditText.cursorPositionAsFlow() = callbackFlow {
    trySend(0)
    val listener: (Int) -> Unit = { trySend(it) }
    setOnCursorPositionChangedListener(listener)
    awaitClose { setOnCursorPositionChangedListener(null) }
}

fun EditText.textAsFlow(emitInitialText: Boolean = false): Flow<CharSequence?> = callbackFlow {
    if (emitInitialText) trySend(text)
    val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
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

fun MaterialSwitch.setupColors(
    thumbCheckedColor: Int = context.colorAttributeResource(R.attr.notoBackgroundColor),
    thumbUnCheckedColor: Int = context.colorAttributeResource(R.attr.notoSecondaryColor),
    trackCheckedColor: Int = context.colorAttributeResource(R.attr.notoPrimaryColor),
    trackUnCheckedColor: Int = context.colorAttributeResource(R.attr.notoSurfaceColor),
) {
    val state =
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
    val thumbColors = intArrayOf(thumbCheckedColor, thumbUnCheckedColor)
    val trackColors = intArrayOf(trackCheckedColor, trackUnCheckedColor)
    thumbTintList = ColorStateList(state, thumbColors)
    trackTintList = ColorStateList(state, trackColors)
}

fun @receiver:ColorInt Int.withDefaultAlpha(alpha: Int = 32): Int =
    ColorUtils.setAlphaComponent(this, alpha)

@SuppressLint("ClickableViewAccessibility")
@Suppress("NOTHING_TO_OVERRIDE", "ACCIDENTAL_OVERRIDE")
inline fun BottomAppBar.setOnSwipeGestureListener(crossinline callback: () -> Unit) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = e2.y - e1.y
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

fun View.isLayoutVisible(rootView: View): Boolean {
    val rect = Rect()
    rootView.getHitRect(rect)
    return getLocalVisibleRect(rect)
}

fun TextView.getDisplayedTextIndex(scrollPosition: Int): Int {
    val lineNumber = layout?.getLineForVertical(scrollPosition) ?: 0
    val start = layout?.getLineStart(lineNumber) ?: 0
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

fun View.isFocusedAsFlow(compositeListener: OnFocusChangedCompositeListener) = callbackFlow {
    val listener = View.OnFocusChangeListener { _, hasFocus -> trySend(hasFocus) }
    compositeListener.registerListener(listener)
    onFocusChangeListener = compositeListener
    awaitClose {
        compositeListener.unregisterListener(listener)
        onFocusChangeListener = null
    }
}.onStart { emit(isFocused) }

fun View.disable() {
    animate()
        .setDuration(DefaultAnimationDuration)
        .alpha(0.5F)
        .withEndAction { isEnabled = false }
}

fun View.enable() {
    animate()
        .setDuration(DefaultAnimationDuration)
        .alpha(1F)
        .withEndAction { isEnabled = true }
}

@SuppressLint("ClickableViewAccessibility")
@Suppress("NOTHING_TO_OVERRIDE", "ACCIDENTAL_OVERRIDE")
inline fun View.setOnSwipeGestureListener(
    crossinline onSwipeLeft: () -> Unit,
    crossinline onSwipeRight: () -> Unit,
    threshold: Float = SwipeGestureThreshold,
) {
    val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val x1 = e1.x
            val x2 = e2.x
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

@Suppress("DEPRECATION")
fun View.performClickHapticFeedback() =
    performHapticFeedback(
        HapticFeedbackConstants.VIRTUAL_KEY,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )

@Suppress("DEPRECATION")
fun View.performLongClickHapticFeedback() =
    performHapticFeedback(
        HapticFeedbackConstants.LONG_PRESS,
        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
    )


fun NavController.destinationAsFlow() = callbackFlow {
    val listener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            trySend(destination)
        }
    addOnDestinationChangedListener(listener)
    awaitClose { removeOnDestinationChangedListener(listener) }
}

fun isCurrentLocaleArabic(): Boolean {
    val appLocale = Locale.getDefault()
    val configuration = Resources.getSystem()?.configuration ?: return false
    val locales = ConfigurationCompat.getLocales(configuration)
    val deviceLocale = locales.get(0)
    return (deviceLocale?.language == "ar" && appLocale.language == "ar") || appLocale.language == "ar"
}

fun CustomEditText.textSelectionAsFlow() = callbackFlow {
    val listener: (String?) -> Unit = { selectedText -> trySend(selectedText) }
    setOnSelectionChangedListener(listener)
    awaitClose { setOnSelectionChangedListener(null) }
}

fun TabLayout.applyEqualWeightForTabs() {
    val viewGroup = getChildAt(0) as ViewGroup?
    for (index in 0 until tabCount) {
        val tab = viewGroup?.getChildAt(index)
        val layoutParams = tab?.layoutParams as? LinearLayout.LayoutParams
        layoutParams?.weight = 1F
        tab?.layoutParams = layoutParams
    }
}

fun Context.highlightText(
    text: String,
    match: String,
    @ColorInt primaryColor: Int = colorAttributeResource(R.attr.notoPrimaryColor),
    @ColorInt secondaryColor: Int = colorAttributeResource(R.attr.notoSecondaryColor),
): Spannable {
    val spannable = text.toSpannable()
    val textStartIndex = 0
    val textEndIndex = text.lastIndex.coerceAtLeast(0)
    val matchStartIndex = spannable.indexOf(match, ignoreCase = true).coerceAtLeast(0)
    val matchEndIndex = matchStartIndex + match.lastIndex.coerceAtLeast(0)
    val primaryColorSpan = ForegroundColorSpan(primaryColor)
    val secondaryColorSpan = ForegroundColorSpan(secondaryColor)
    val boldFontSpan = tryLoadingFontResource(R.font.nunito_black)?.style?.let(::StyleSpan)
    val boldSpan = StyleSpan(Typeface.BOLD)
    spannable.apply {
        setSpan(
            primaryColorSpan,
            textStartIndex,
            matchStartIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(primaryColorSpan, matchEndIndex, textEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(
            secondaryColorSpan,
            matchStartIndex,
            matchEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        setSpan(boldSpan, matchStartIndex, matchEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (boldFontSpan != null) setSpan(
            boldFontSpan,
            matchStartIndex,
            matchEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun BottomAppBar.isHiddenAsFlow() = callbackFlow {
    val listener = HideBottomViewOnScrollBehavior.OnScrollStateChangedListener { _, state ->
        trySend(state == BottomAppBar.Behavior.STATE_SCROLLED_DOWN)
    }
    addOnScrollStateChangedListener(listener)
    awaitClose { removeOnScrollStateChangedListener(listener) }
}

fun TextView.setHighlightedText(text: String, term: String, color: NotoColor, matchIndices: IntRange? = null) {
    val indices = text.indicesOf(term, ignoreCase = true).filter { it.first < it.last }
    val colorResource = context?.colorResource(color.toColorResourceId()) ?: return
    val lightColorResource = context?.colorAttributeResource(R.attr.notoSecondaryColor)?.withDefaultAlpha(DisabledAlpha / 2) ?: return
    val onColorResource = context?.colorAttributeResource(R.attr.notoBackgroundColor) ?: return
    val onLightColorResource = context?.colorAttributeResource(R.attr.notoPrimaryColor) ?: return

    val highlightedText = text.toSpannable().apply {
        indices.forEach { range ->
            val backgroundColorSpan = CustomBackgroundColorSpan(context, colorResource, onColorResource, textSize)
            val lightBackgroundColorSpan = CustomBackgroundColorSpan(context, lightColorResource, onLightColorResource, textSize)
            val boldFontSpan = context.tryLoadingFontResource(R.font.nunito_black)?.style?.let(::StyleSpan)
            val boldSpan = StyleSpan(Typeface.BOLD)
            val startIndex = range.first.coerceIn(0, this.length)
            val endIndex = range.last.coerceIn(0, this.length)
            if (range == matchIndices) {
                setSpan(backgroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(boldSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (boldFontSpan != null) setSpan(boldFontSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                setSpan(lightBackgroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
    this.text = highlightedText
}

fun FloatingActionButton.hideWithAnimation() {
    animate()
        .scaleX(0F)
        .scaleY(0F)
        .alpha(0F)
        .setDuration(DefaultAnimationDuration)
        .withEndAction { isVisible = false }
        .start()
}

fun FloatingActionButton.showWithAnimation() {
    animate()
        .scaleX(1F)
        .scaleY(1F)
        .alpha(1F)
        .setDuration(DefaultAnimationDuration)
        .withEndAction { isVisible = true }
        .start()
}

val TextView.currentLine: Int
    get() {
        val cursorPosition = selectionStart.coerceAtLeast(0)
        return layout?.getLineForOffset(cursorPosition)?.coerceIn(0, lineCount) ?: 0
    }

val TextView.currentLineScrollPosition: Int
    get() = layout?.getLineTop(currentLine) ?: 0