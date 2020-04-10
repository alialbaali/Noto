package com.noto.util

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.noto.R
import com.noto.database.NotoColor

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
    this.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))
    this.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))
}

fun <T : Any, VH : RecyclerView.ViewHolder> RecyclerView.setList(
    list: List<T>?,
    rvAdapter: ListAdapter<T, VH>,
    view: View
) {
    list?.let {
        if (list.isEmpty()){
            this.visibility = View.GONE
            view.visibility = View.VISIBLE
        } else {
            this.visibility = View.VISIBLE
            view.visibility = View.GONE
            rvAdapter.submitList(list)
        }
    }
}