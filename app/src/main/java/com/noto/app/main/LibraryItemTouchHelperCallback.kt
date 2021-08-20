package com.noto.app.main

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.util.LayoutManager

class LibraryItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val layoutManager: LayoutManager,
    private val callback: (RecyclerView, EpoxyViewHolder, EpoxyViewHolder) -> Unit,
) : EpoxyModelTouchCallback<LibraryItem>(epoxyController, LibraryItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: LibraryItem?, adapterPosition: Int): Int {
        val dragFlags = when (layoutManager) {
            LayoutManager.Linear -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
            LayoutManager.Grid -> ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {
        callback(recyclerView, viewHolder as EpoxyViewHolder, target as EpoxyViewHolder)
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun onDragStarted(model: LibraryItem?, itemView: View?, adapterPosition: Int) {
        itemView?.animate()
            ?.scaleX(0.95F)
            ?.scaleY(0.95F)
            ?.setDuration(50)
            ?.start()
        super.onDragStarted(model, itemView, adapterPosition)
    }

    override fun clearView(model: LibraryItem?, itemView: View?) {
        itemView?.animate()
            ?.scaleX(1F)
            ?.scaleY(1F)
            ?.setDuration(50)
            ?.start()
        super.clearView(model, itemView)
    }
}