package com.noto.todo.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

internal class TodolistItemTouchHelper(private val todolistItemTouchHelperAdapter: TodoListItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        todolistItemTouchHelperAdapter.onMoveViewHolder(viewHolder as TodolistItemViewHolder, target as TodolistItemViewHolder)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        todolistItemTouchHelperAdapter.onClearViewHolder(viewHolder as TodolistItemViewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        return
    }
}

internal interface TodoListItemTouchHelperAdapter {

    fun onMoveViewHolder(fromViewHolder: TodolistItemViewHolder, toViewHolder: TodolistItemViewHolder)

    fun onClearViewHolder(viewHolder: TodolistItemViewHolder)
}