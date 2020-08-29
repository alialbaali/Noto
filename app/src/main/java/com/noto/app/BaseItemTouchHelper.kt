package com.noto.app

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseItemTouchHelper(private val listener: BaseItemTouchHelperListener) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    open override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    abstract override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        listener.onMoveViewHolder(viewHolder, target)
        return false
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        listener.onClearViewHolder(viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwipeViewHolder(viewHolder)
        return
    }
}

class LibraryItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }
}

class NotoItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }
}

class BlockItemTouchHelper(private val listener: BaseItemTouchHelperListener) : BaseItemTouchHelper(listener) {

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

}

interface BaseItemTouchHelperListener {

    fun onMoveViewHolder(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder)

    fun onSwipeViewHolder(viewHolder: RecyclerView.ViewHolder) {}

    fun onClearViewHolder(viewHolder: RecyclerView.ViewHolder)
}