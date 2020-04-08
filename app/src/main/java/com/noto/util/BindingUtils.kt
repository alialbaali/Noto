package com.noto.util

import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:checkBox", "app:isChecked", "app:updateTodo", requireAll = false)
fun TextView.onCheck(checkBox: CheckBox, isChecked: Boolean, updateTodo: Runnable?) {
    if (isChecked) {
        this.setChecked()
    } else {
        this.setUnchecked()
    }
    checkBox.setOnClickListener {
        if (checkBox.isChecked) {
            this.setChecked()
        } else {
            this.setUnchecked()
        }
        updateTodo?.run()
    }
}