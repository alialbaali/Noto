package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.NotoColor

fun Resources.colorStateResource(@ColorRes id: Int): ColorStateList? = ResourcesCompat.getColorStateList(this, id, null)
fun Resources.colorResource(@ColorRes id: Int): Int = ResourcesCompat.getColor(this, id, null)
fun Resources.stringResource(@StringRes id: Int): String = getString(id)
fun Resources.stringResource(@StringRes id: Int, vararg formatArgs: Any?): String = getString(id, *formatArgs)
fun Resources.drawableResource(@DrawableRes id: Int): Drawable? = ResourcesCompat.getDrawable(this, id, null)
fun Resources.dimenResource(@DimenRes id: Int): Float = getDimension(id)
fun Resources.fontResource(context: Context, @FontRes id: Int) = ResourcesCompat.getFont(context, id)
fun Resources.pluralsResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) = getQuantityString(id, quantity, *formatArgs)

fun Context.tryLoadingFontResource(@FontRes id: Int) = try {
    resources.fontResource(this, id)
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
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()