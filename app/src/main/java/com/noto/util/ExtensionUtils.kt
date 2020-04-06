package com.noto.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.widget.EditText
import com.noto.R
import com.noto.database.NotoColor

fun NotoColor.getColorPrimary(context: Context): ColorStateList {
    return when (this) {
        NotoColor.GRAY -> ColorStateList.valueOf(context.getColor(R.color.colorPrimaryGray))
        NotoColor.BLUE -> ColorStateList.valueOf(context.getColor(R.color.colorPrimaryBlue))
        NotoColor.PINK -> ColorStateList.valueOf(context.getColor(R.color.colorPrimaryPink))
        NotoColor.CYAN -> ColorStateList.valueOf(context.getColor(R.color.colorPrimaryCyan))
    }
}

fun NotoColor.getColorOnPrimary(context: Context): ColorStateList {
    return when (this) {
        NotoColor.GRAY -> ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryGray))
        NotoColor.BLUE -> ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryBlue))
        NotoColor.PINK -> ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryPink))
        NotoColor.CYAN -> ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryCyan))
    }
}

fun EditText.setChecked() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    this.setTextColor(resources.getColor(R.color.colorOnPrimary_600, null))
}

fun EditText.setUnchecked() {
    this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    this.setTextColor(resources.getColor(R.color.colorOnPrimary_900, null))
}
