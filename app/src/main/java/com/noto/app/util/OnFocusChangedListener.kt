package com.noto.app.util

import android.view.View
import android.view.View.OnFocusChangeListener

class OnFocusChangedCompositeListener : OnFocusChangeListener {
    private val listeners = mutableListOf<OnFocusChangeListener>()

    fun registerListener(listener: OnFocusChangeListener) = listeners.add(listener)

    fun unregisterListener(listener: OnFocusChangeListener) = listeners.remove(listener)

    override fun onFocusChange(v: View?, hasFocus: Boolean) = listeners.forEach { listener ->
        listener.onFocusChange(v, hasFocus)
    }
}