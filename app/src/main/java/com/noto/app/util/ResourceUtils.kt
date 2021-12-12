package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.NotoColor

fun Context.colorStateResource(@ColorRes id: Int) = ResourcesCompat.getColorStateList(resources, id, null)
fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) = getString(id, *formatArgs)
fun Context.drawableResource(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)
fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.fontResource(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
fun Context.pluralsResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) = resources.getQuantityString(id, quantity, *formatArgs)
fun Context.attributeColoResource(@AttrRes id: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(id, typedValue, true)
    return typedValue.data
}

fun @receiver:ColorInt Int.toColorStateList() = ColorStateList.valueOf(this)

fun Context.tryLoadingFontResource(@FontRes id: Int) = try {
    fontResource(id)
} catch (exception: Throwable) {
    null
}

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
    NotoColor.Indigo -> R.color.colorAccentIndigo
    NotoColor.DeepPurple -> R.color.colorAccentDeepPurple
    NotoColor.DeepOrange -> R.color.colorAccentDeepOrange
    NotoColor.DeepGreen -> R.color.colorAccentDeepGreen
    NotoColor.LightBlue -> R.color.colorAccentLightBlue
    NotoColor.LightGreen -> R.color.colorAccentLightGreen
    NotoColor.LightRed -> R.color.colorAccentLightRed
    NotoColor.LightPink -> R.color.colorAccentLightPink
    NotoColor.Black -> R.color.colorAccentBlack
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()