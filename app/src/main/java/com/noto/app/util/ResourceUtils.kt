package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.noto.app.R
import com.noto.app.domain.model.*
import com.noto.app.filtered.FilteredItemModel

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

fun NotoColor.toColorResourceId(): Int = when (this) {
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

fun Icon.toDrawableResourceId(): Int = when (this) {
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

@Suppress("DEPRECATION")
fun Language.toStringResourceId(): Int = when (this) {
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
    Language.Korean -> R.string.korean
}

fun ScreenBrightnessLevel.toStringResourceId(): Int = when (this) {
    ScreenBrightnessLevel.System -> R.string.follow_system
    ScreenBrightnessLevel.Min -> R.string.min
    ScreenBrightnessLevel.VeryLow -> R.string.very_low
    ScreenBrightnessLevel.Low -> R.string.low
    ScreenBrightnessLevel.Medium -> R.string.medium
    ScreenBrightnessLevel.High -> R.string.high
    ScreenBrightnessLevel.VeryHigh -> R.string.very_high
    ScreenBrightnessLevel.Max -> R.string.max
}

fun FilteringType.toStringResourceId(): Int = when (this) {
    FilteringType.Inclusive -> R.string.inclusive
    FilteringType.Exclusive -> R.string.exclusive
    FilteringType.Strict -> R.string.strict
}

fun FilteringType.toDescriptionResourceId(): Int = when (this) {
    FilteringType.Inclusive -> R.string.inclusive_description
    FilteringType.Exclusive -> R.string.exclusive_description
    FilteringType.Strict -> R.string.strict_description
}

fun SortingOrder.toStringResourceId(): Int = when (this) {
    SortingOrder.Ascending -> R.string.ascending
    SortingOrder.Descending -> R.string.descending
}

fun GroupingOrder.toStringResourceId(): Int = when (this) {
    GroupingOrder.Ascending -> R.string.ascending
    GroupingOrder.Descending -> R.string.descending
}

fun Grouping.toStringResourceId(): Int = when (this) {
    Grouping.None -> R.string.none
    Grouping.CreationDate -> R.string.creation_date
    Grouping.Label -> R.string.label
    Grouping.AccessDate -> R.string.access_date
}

fun NoteListSortingType.toStringResourceId(): Int = when (this) {
    NoteListSortingType.Manual -> R.string.manual
    NoteListSortingType.CreationDate -> R.string.creation_date
    NoteListSortingType.Alphabetical -> R.string.alphabetical
    NoteListSortingType.AccessDate -> R.string.access_date
}

fun FolderListSortingType.toStringResourceId(): Int = when (this) {
    FolderListSortingType.Manual -> R.string.manual
    FolderListSortingType.CreationDate -> R.string.creation_date
    FolderListSortingType.Alphabetical -> R.string.alphabetical
}

fun Theme.toStringResourceId(): Int = when (this) {
    Theme.System -> R.string.system_dark_theme
    Theme.SystemBlack -> R.string.system_black_theme
    Theme.Light -> R.string.light_theme
    Theme.Dark -> R.string.dark_theme
    Theme.Black -> R.string.black_theme
}

fun Icon.toStringResourceId(): Int = when (this) {
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

fun Font.toStringResourceId(): Int = when (this) {
    Font.Nunito -> R.string.nunito
    Font.Monospace -> R.string.monospace
}

fun FilteredItemModel.toStringResourceId(): Int = when (this) {
    FilteredItemModel.All -> R.string.all
    FilteredItemModel.Recent -> R.string.recent
    FilteredItemModel.Scheduled -> R.string.scheduled
    FilteredItemModel.Archived -> R.string.archived
}

fun VaultTimeout.toStringResourceId(): Int = when (this) {
    VaultTimeout.Immediately -> R.string.immediately
    VaultTimeout.OnAppClose -> R.string.on_app_close
    VaultTimeout.After1Hour -> R.string.after_1_hour
    VaultTimeout.After4Hours -> R.string.after_4_hours
    VaultTimeout.After12Hours -> R.string.after_12_hours
}

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()