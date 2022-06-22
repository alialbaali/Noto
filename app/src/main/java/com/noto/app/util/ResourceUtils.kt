package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.NotoColor

fun Context.colorStateListResource(@ColorRes id: Int) = ResourcesCompat.getColorStateList(resources, id, null)
fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) = getString(id, *formatArgs)
fun Context.drawableResource(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)
fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.fontResource(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
fun Context.quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) = resources.getQuantityString(id, quantity, *formatArgs)
fun Context.colorAttributeResource(@AttrRes id: Int): Int {
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

fun Icon.toResource(): Int = when (this) {
    Icon.Futuristic -> R.mipmap.ic_launcher_futuristic
    Icon.DarkRain -> R.mipmap.ic_launcher_dark_rain
    Icon.Airplane -> R.mipmap.ic_launcher_airplane
    Icon.BlossomIce -> R.mipmap.ic_launcher_blossom_ice
    Icon.DarkAlpine -> R.mipmap.ic_launcher_dark_alpine
    Icon.DarkSide -> R.mipmap.ic_launcher_dark_side
    Icon.Earth -> R.mipmap.ic_launcher_earth
    Icon.Fire -> R.mipmap.ic_launcher_fire
    Icon.Purpleberry -> R.mipmap.ic_launcher_purpleberry
    Icon.SanguineSun -> R.mipmap.ic_launcher_sanguine_sun
}

fun Icon.toTitle(): Int = when (this) {
    Icon.Futuristic -> R.string.futuristic
    Icon.DarkRain -> R.string.dark_rain
    Icon.Airplane -> R.string.airplane
    Icon.BlossomIce -> R.string.blossom_ice
    Icon.DarkAlpine -> R.string.dark_alpine
    Icon.DarkSide -> R.string.dark_side
    Icon.Earth -> R.string.earth
    Icon.Fire -> R.string.fire
    Icon.Purpleberry -> R.string.purpleberry
    Icon.SanguineSun -> R.string.sanguine_sun
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()