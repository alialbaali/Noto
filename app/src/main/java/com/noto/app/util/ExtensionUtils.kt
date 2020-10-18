package com.noto.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.noto.app.R
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun View.snackbar(message: String) = Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
    animationMode = Snackbar.ANIMATION_MODE_SLIDE
    setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
    setTextColor(ResourcesCompat.getColor(resources, R.color.colorBackground, null))
}

fun View.toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT)

fun CollapsingToolbarLayout.setFontFamily() {
    setCollapsedTitleTypeface(ResourcesCompat.getFont(requireNotNull(context), R.font.arima_madurai_bold))
    setExpandedTitleTypeface(ResourcesCompat.getFont(requireNotNull(context), R.font.arima_madurai_medium))
}

fun SharedPreferences.setValue(key: String, value: Any) {
    this.edit {
        when (value) {
            is Boolean -> this.putBoolean(key, value)
            is String -> this.putString(key, value)
            is Float -> this.putFloat(key, value)
            is Long -> this.putLong(key, value)
            is Int -> this.putInt(key, value)
        }
        apply()
    }
}

fun SharedPreferences.getValue(key: String): Any? {
    return this.all[key]
}

fun AlarmManager.setAlarm(type: Int, timeInMills: Long, pendingIntent: PendingIntent) = AlarmManagerCompat.setExactAndAllowWhileIdle(this, type, timeInMills, pendingIntent)

fun Noto.isValid(): Boolean = !(notoTitle.isBlank() && notoBody.isBlank())

fun <T> MutableLiveData<T>.notifyObserver() {
    value = value
}

fun Int.dp(context: Context): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics)

fun NotoColor.getValue(): Int = when (this) {
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

fun NotoIcon.getValue(): Int = when (this) {
    NotoIcon.NOTEBOOK -> R.drawable.ic_notebook_24dp
    NotoIcon.LIST -> R.drawable.ic_list_24dp
    NotoIcon.FITNESS -> R.drawable.ic_fitness_24dp
    NotoIcon.HOME -> R.drawable.ic_home_24dp
    NotoIcon.BOOK -> R.drawable.ic_book_24dp
    NotoIcon.SCHOOL -> R.drawable.ic_school_24dp
    NotoIcon.WORK -> R.drawable.ic_work_24dp
    NotoIcon.LAPTOP -> R.drawable.ic_laptop_24dp
    NotoIcon.GROCERY -> R.drawable.ic_grocery_24dp
    NotoIcon.SHOP -> R.drawable.ic_shop_24dp
    NotoIcon.GAME -> R.drawable.ic_game_24dp
    NotoIcon.TRAVEL -> R.drawable.ic_travel_24dp
    NotoIcon.MUSIC -> R.drawable.ic_music_24dp
    NotoIcon.IDEA -> R.drawable.ic_idea_24dp
    NotoIcon.WRENCH -> R.drawable.ic_wrench_24dp
    NotoIcon.CHART -> R.drawable.ic_chart_24dp
    NotoIcon.CALENDAR -> R.drawable.ic_calendar_24dp
    NotoIcon.CODE -> R.drawable.ic_code_24dp
}