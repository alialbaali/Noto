package com.noto.todo.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.databinding.ListItemTodolistBinding
import com.noto.todo.model.Todolist
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary

internal class TodolistListRVAdapter(private val navigateToTodolist: NavigateToTodolist) :

    ListAdapter<Todolist, TodolistItemViewHolder>(TodolistItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistItemViewHolder {
        return TodolistItemViewHolder.create(parent, navigateToTodolist)
    }

    override fun onBindViewHolder(holder: TodolistItemViewHolder, position: Int) {
        val todolist = getItem(position)
        holder.bind(todolist)
        holder.todolist = todolist
    }

}

internal class TodolistItemViewHolder(
    private val binding: ListItemTodolistBinding,
    private val navigateToTodolist: NavigateToTodolist
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var todolist: Todolist

    init {
        binding.root.setOnClickListener {
            navigateToTodolist.navigate(todolist)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToTodolist: NavigateToTodolist
        ): TodolistItemViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemTodolistBinding.inflate(layoutInflater, parent, false)

            return TodolistItemViewHolder(binding, navigateToTodolist)
        }
    }

    fun bind(todolist: Todolist) {
        binding.todolist = todolist
        binding.executePendingBindings()

        val colorPrimary = todolist.notoColor.getColorPrimary(binding.root.context)
        val colorOnPrimary = todolist.notoColor.getColorOnPrimary(binding.root.context)

        val drawable = binding.root.resources.getDrawable(R.drawable.shape_todolist_item, null)
        val ripple = RippleDrawable(ColorStateList.valueOf(colorPrimary), drawable, drawable)

        binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(colorOnPrimary)
        binding.todoListTv.setTextColor(colorOnPrimary)
        binding.root.background = ripple
    }

}

private class TodolistItemDiffCallback : DiffUtil.ItemCallback<Todolist>() {

    override fun areItemsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem.todolistId == newItem.todolistId
    }

    override fun areContentsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem == newItem
    }
    
}

interface NavigateToTodolist {
    fun navigate(todolist: Todolist)
}