package com.noto.app.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var selectedText: String? = null
        set(value) {
            if (field != value) {
                field = value
                selectionChangedListener?.invoke(value)
            }
        }

    private var selectionChangedListener: ((String?) -> Unit)? = null
        set(value) {
            field = value
            field?.invoke(selectedText)
        }
    
    private var cursorPositionChangedListener: ((Int) -> Unit)? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        cursorPositionChangedListener?.invoke(selStart)
        val startIndex = minOf(selStart, selEnd)
        val endIndex = maxOf(selStart, selEnd)
        selectedText = if (startIndex != -1 && endIndex != -1) {
            text.toString().substring(startIndex, endIndex)
        } else {
            null
        }
    }

    fun setOnSelectionChangedListener(listener: ((String?) -> Unit)?) {
        this.selectionChangedListener = listener
    }
    
    fun setOnCursorPositionChangedListener(listener: ((Int) -> Unit)?) {
        this.cursorPositionChangedListener = listener
    }
}