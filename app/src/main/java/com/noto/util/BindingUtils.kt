package com.noto.util

import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.noto.database.NotoColor

@BindingAdapter("app:notoColor")
fun View.setNotoColor(notoColor: NotoColor) {
    when (this) {
        is ImageView -> {
            this.imageTintList = notoColor.getColorOnPrimary(this.context)
        }
        is CheckBox -> {
            this.backgroundTintList = notoColor.getColorOnPrimary(this.context)
        }
        else -> {
            this.backgroundTintList = notoColor.getColorPrimary(this.context)
        }
    }
}

@BindingAdapter("app:onCheck", "app:isChecked")
fun EditText.onCheck(checkBox: CheckBox, isChecked: Boolean) {
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
    }
}

