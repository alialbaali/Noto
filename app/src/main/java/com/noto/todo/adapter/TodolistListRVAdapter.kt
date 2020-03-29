package com.noto.todo.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.ListItemTodolistBinding
import com.noto.todo.model.Todolist

internal class TodolistListRVAdapter(
    private val context: Context,
    private val navigateToTodolist: NavigateToTodolist
) :
    ListAdapter<Todolist, TodolistItemViewHolder>(TodolistItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistItemViewHolder {
        return TodolistItemViewHolder.create(parent, context, navigateToTodolist)
    }

    override fun onBindViewHolder(holder: TodolistItemViewHolder, position: Int) {
        val todolist = getItem(position)
        holder.bind(todolist)
        holder.item = todolist
    }


}

internal class TodolistItemViewHolder(
    private val binding: ListItemTodolistBinding,
    private val context: Context,
    private val navigateToTodolist: NavigateToTodolist
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var item: Todolist

    init {
        binding.root.setOnClickListener {
            navigateToTodolist.navigate(item.todolistId)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            context: Context,
            navigateToTodolist: NavigateToTodolist
        ): TodolistItemViewHolder {
            return TodolistItemViewHolder(
                ListItemTodolistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context, navigateToTodolist
            )
        }
    }

    fun bind(item: Todolist) {
        binding.todoListTv.text = item.todolistTitle

        when (item.notoColor) {

            NotoColor.GRAY -> {
                binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorPrimaryGray,
                        null
                    )
                )
            }
            NotoColor.BLUE -> {
                binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorPrimaryBlue,
                        null
                    )
                )
            }
            NotoColor.PINK -> {
                binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorPrimaryPink,
                        null
                    )
                )
            }
            NotoColor.CYAN -> {
                binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(
                    context.resources.getColor(
                        R.color.colorPrimaryCyan,
                        null
                    )
                )
            }
        }
    }

}

private class TodolistItemDiffCallback() : DiffUtil.ItemCallback<Todolist>() {
    override fun areItemsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem.todolistId == newItem.todolistId
    }

    override fun areContentsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem == newItem
    }
}

interface NavigateToTodolist {
    fun navigate(todolistId: Long)
}