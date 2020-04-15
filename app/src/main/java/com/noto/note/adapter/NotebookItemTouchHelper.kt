package com.noto.note.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

internal class NotebookItemTouchHelperCallback(private val notebookItemTouchHelperAdapter: NotebookItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        notebookItemTouchHelperAdapter.onMoveViewHolder(viewHolder as NotebookItemViewHolder, target as NotebookItemViewHolder)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        notebookItemTouchHelperAdapter.onClearViewHolder(viewHolder as NotebookItemViewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        return
    }
}

internal interface NotebookItemTouchHelperAdapter {

    fun onMoveViewHolder(fromViewHolder: NotebookItemViewHolder, toViewHolder: NotebookItemViewHolder)

    fun onClearViewHolder(viewHolder: NotebookItemViewHolder)
}