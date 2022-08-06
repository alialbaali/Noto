package com.noto.app.util

import android.content.Context
import android.util.AttributeSet
import com.airbnb.epoxy.EpoxyRecyclerView

private const val AutoScrollDuration = 2000L

class PreviewRecyclerView : EpoxyRecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        isNestedScrollingEnabled = false
    }
}