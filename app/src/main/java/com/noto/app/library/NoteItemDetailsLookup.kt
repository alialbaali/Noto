package com.noto.app.library

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyViewHolder

class NoteItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        return if (view != null) {
            val viewHolder = recyclerView.getChildViewHolder(view) as EpoxyViewHolder
            val model = viewHolder.model as NoteItem
            model.itemDetails
        } else {
            null
        }
    }

    private val NoteItem.itemDetails
        get() = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = note.position
            override fun getSelectionKey(): Long = note.id
        }
}