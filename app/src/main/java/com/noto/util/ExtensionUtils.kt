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
import com.noto.database.SortMethod
import com.noto.note.model.Notebook
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

fun List<Notebook>.sortAsc(sortMethod: SortMethod): List<Notebook> {
    return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedBy { it.notebookTitle }
        SortMethod.CreationDate -> this.sortedBy { it.notebookCreationDate }
        SortMethod.ModificationDate -> this.sortedBy { it.notebookModificationDate }
        SortMethod.Custom -> this.sortedBy { it.notebookPosition }
        else -> this.sortedBy { it.notebookPosition }
    }
}

fun List<Notebook>.sortDesc(sortMethod: SortMethod): List<Notebook> {
   return when (sortMethod) {
        SortMethod.Alphabetically -> this.sortedByDescending { it.notebookTitle }
        SortMethod.CreationDate -> this.sortedByDescending { it.notebookCreationDate }
        SortMethod.ModificationDate -> this.sortedByDescending { it.notebookModificationDate }
        SortMethod.Custom -> this.sortedByDescending { it.notebookPosition }
       else -> this.sortedByDescending { it.notebookPosition }
   }
}