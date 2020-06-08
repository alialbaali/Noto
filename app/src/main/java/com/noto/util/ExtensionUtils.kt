package com.noto.util

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.noto.R
import com.noto.domain.model.*

//fun TextView.setChecked() {
//    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//    this.setTextColor(resources.getColor(R.color.colorOnPrimaryGray, null))
//}
//
//fun TextView.setUnchecked() {
//    this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
//    this.setTextColor(resources.getColor(R.color.colorOnPrimary_900, null))
//}

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> = this

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).apply {
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        setBackgroundTint(resources.getColor(R.color.colorPrimary))
        setTextColor(resources.getColor(R.color.colorBackground))
    }.show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun NotoColor.getValue(): Int {
    return when (this) {
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
}

fun NotoIcon.getValue(): Int {
    return when (this) {
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
}


fun CollapsingToolbarLayout.setFontFamily() {
    this.setCollapsedTitleTypeface(ResourcesCompat.getFont(requireNotNull(context), R.font.roboto_bold))
    this.setExpandedTitleTypeface(ResourcesCompat.getFont(requireNotNull(context), R.font.roboto_medium))
}

fun <T : Any, VH : RecyclerView.ViewHolder> RecyclerView.setList(list: List<T>?, rvAdapter: ListAdapter<T, VH>, view: View) {
    list?.let {
        if (list.isEmpty()) {
            this.visibility = View.GONE
            view.visibility = View.VISIBLE
        } else {
            this.visibility = View.VISIBLE
            view.visibility = View.GONE
            rvAdapter.submitList(list)
        }
    }
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

fun List<Library>.sortAsc(sortMethod: SortMethod): List<Library> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedBy { it.libraryTitle }
        SortMethod.CreationDate -> this.sortedBy { it.libraryCreationDate }
        SortMethod.Custom -> this.sortedBy { it.libraryPosition }
        else -> this.sortedBy { it.libraryPosition }
    }
}

fun List<Library>.sortDesc(sortMethod: SortMethod): List<Library> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedByDescending { it.libraryTitle }
        SortMethod.CreationDate -> this.sortedByDescending { it.libraryCreationDate }
        SortMethod.Custom -> this.sortedByDescending { it.libraryPosition }
        else -> this.sortedByDescending { it.libraryPosition }
    }
}

fun List<Noto>.sortAscNoto(sortMethod: SortMethod): List<Noto> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedBy { it.notoTitle }
        SortMethod.CreationDate -> this.sortedBy { it.notoCreationDate }
        SortMethod.Custom -> this.sortedBy { it.notoPosition }
        else -> this.sortedBy { it.notoPosition }
    }
}

fun List<Noto>.sortDescNoto(sortMethod: SortMethod): List<Noto> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedByDescending { it.libraryId }
        SortMethod.CreationDate -> this.sortedByDescending { it.notoCreationDate }
        SortMethod.Custom -> this.sortedByDescending { it.notoPosition }
        else -> this.sortedByDescending { it.notoPosition }
    }
}