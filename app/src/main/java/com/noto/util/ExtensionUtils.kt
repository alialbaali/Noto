package com.noto.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.noto.R
import com.noto.database.NotoColor
import com.noto.database.NotoIcon
import com.noto.database.SortMethod
import com.noto.note.model.Notebook
import com.noto.todo.model.Todolist
import timber.log.Timber

fun NotoColor.getColorPrimary(context: Context): Int {
    return when (this) {
        NotoColor.GRAY -> context.getColor(R.color.colorPrimaryGray)
        NotoColor.BLUE -> context.getColor(R.color.colorPrimaryBlue)
        NotoColor.PINK -> context.getColor(R.color.colorPrimaryPink)
        NotoColor.CYAN -> context.getColor(R.color.colorPrimaryCyan)
    }
}

fun NotoColor.getColorOnPrimary(context: Context): Int {
    return when (this) {
        NotoColor.GRAY -> context.getColor(R.color.colorOnPrimaryGray)
        NotoColor.BLUE -> context.getColor(R.color.colorOnPrimaryBlue)
        NotoColor.PINK -> context.getColor(R.color.colorOnPrimaryPink)
        NotoColor.CYAN -> context.getColor(R.color.colorOnPrimaryCyan)
    }
}

fun TextView.setChecked() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    this.setTextColor(resources.getColor(R.color.colorOnPrimaryGray, null))
}

fun TextView.setUnchecked() {
    this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    this.setTextColor(resources.getColor(R.color.colorOnPrimary_900, null))
}

fun Activity.setStatusBarColor(notoColor: NotoColor) {
    this.window.statusBarColor = notoColor.getColorPrimary(this)
}

fun CollapsingToolbarLayout.setFontFamily() {
    this.setCollapsedTitleTypeface(
        ResourcesCompat.getFont(
            requireNotNull(context),
            R.font.roboto_bold
        )
    )
    this.setExpandedTitleTypeface(
        ResourcesCompat.getFont(
            requireNotNull(context),
            R.font.roboto_medium
        )
    )
}

fun <T : Any, VH : RecyclerView.ViewHolder> RecyclerView.setList(
    list: List<T>?,
    rvAdapter: ListAdapter<T, VH>,
    view: View
) {
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
    Timber.i("VALUE = ${this.all[key]}")
    return this.all[key]
}

fun List<Notebook>.sortNotebookAsc(sortMethod: SortMethod): List<Notebook> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedBy { it.notebookTitle }
        SortMethod.CreationDate -> this.sortedBy { it.notebookCreationDate }
        SortMethod.ModificationDate -> this.sortedBy { it.notebookModificationDate }
        SortMethod.Custom -> this.sortedBy { it.notebookPosition }
        else -> this.sortedBy { it.notebookPosition }
    }
}

fun List<Notebook>.sortNotebookDesc(sortMethod: SortMethod): List<Notebook> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedByDescending { it.notebookTitle }
        SortMethod.CreationDate -> this.sortedByDescending { it.notebookCreationDate }
        SortMethod.ModificationDate -> this.sortedByDescending { it.notebookModificationDate }
        SortMethod.Custom -> this.sortedByDescending { it.notebookPosition }
        else -> this.sortedByDescending { it.notebookPosition }
    }
}

fun List<Todolist>.sortTodolistAsc(sortMethod: SortMethod): List<Todolist> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedBy { it.todolistTitle }
        SortMethod.CreationDate -> this.sortedBy { it.todolistCreationDate }
        SortMethod.ModificationDate -> this.sortedBy { it.todolistModificationDate }
        SortMethod.Custom -> this.sortedBy { it.todolistPosition }
        else -> this.sortedBy { it.todolistPosition }
    }
}

fun List<Todolist>.sortTodolistDesc(sortMethod: SortMethod): List<Todolist> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedByDescending { it.todolistTitle }
        SortMethod.CreationDate -> this.sortedByDescending { it.todolistCreationDate }
        SortMethod.ModificationDate -> this.sortedByDescending { it.todolistModificationDate }
        SortMethod.Custom -> this.sortedByDescending { it.todolistPosition }
        else -> this.sortedByDescending { it.todolistPosition }
    }
}

fun NotoIcon.getImageResource(): Int {
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
    }
}