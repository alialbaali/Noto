package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.ScreenBrightnessLevel

fun Context.colorStateListResource(@ColorRes id: Int) = ResourcesCompat.getColorStateList(resources, id, null)
fun Context.colorResource(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, null)
fun Context.stringResource(@StringRes id: Int, vararg formatArgs: Any? = emptyArray()) = getString(id, *formatArgs)
fun Context.drawableResource(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)
fun Context.dimenResource(@DimenRes id: Int) = resources.getDimension(id)
fun Context.fontResource(@FontRes id: Int) = ResourcesCompat.getFont(this, id)
fun Context.quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any?) =
    resources.getQuantityString(id, quantity, *formatArgs)

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

fun Language.toResource(): Int = when (this) {
    Language.System -> R.string.follow_system
    Language.English -> R.string.english
    Language.Turkish -> R.string.turkish
    Language.Arabic -> R.string.arabic
    Language.Indonesian -> R.string.indonesian
    Language.Russian -> R.string.russian
    Language.Tamil -> R.string.tamil
    Language.Spanish -> R.string.spanish
    Language.French -> R.string.french
    Language.German -> R.string.german
    Language.Italian -> R.string.italian
    Language.Czech -> R.string.czech
    Language.Lithuanian -> R.string.lithuanian
    Language.SimplifiedChinese -> R.string.simplified_chinese
    Language.Portuguese -> R.string.portuguese
}

fun ScreenBrightnessLevel.toResource(): Int = when (this) {
    ScreenBrightnessLevel.System -> R.string.follow_system
    ScreenBrightnessLevel.Min -> R.string.min
    ScreenBrightnessLevel.VeryLow -> R.string.very_low
    ScreenBrightnessLevel.Low -> R.string.low
    ScreenBrightnessLevel.Medium -> R.string.medium
    ScreenBrightnessLevel.High -> R.string.high
    ScreenBrightnessLevel.VeryHigh -> R.string.very_high
    ScreenBrightnessLevel.Max -> R.string.max
}

fun FilteringType.toResource(): Int = when (this) {
    FilteringType.Inclusive -> R.string.inclusive
    FilteringType.Exclusive -> R.string.exclusive
    FilteringType.Strict -> R.string.strict
}

fun FilteringType.toDescriptionResource(): Int = when (this) {
    FilteringType.Inclusive -> R.string.inclusive_description
    FilteringType.Exclusive -> R.string.exclusive_description
    FilteringType.Strict -> R.string.strict_description
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()