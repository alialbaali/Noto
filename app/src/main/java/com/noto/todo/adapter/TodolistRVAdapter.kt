package com.noto.todo.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.database.NotoColor
import com.noto.databinding.ListItemTodoBinding
import com.noto.todo.model.Todo
import com.noto.todo.viewModel.TodolistViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import com.noto.util.setChecked
import com.noto.util.setUnchecked

class TodolistRVAdapter(
    private val viewModel: TodolistViewModel,
    private val notoColor: NotoColor,
    private val navigateToTodo: NavigateToTodo
) :
    ListAdapter<Todo, TodoItemViewHolder>(TodoItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        return TodoItemViewHolder.create(parent, viewModel, navigateToTodo, notoColor)
    }

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
        holder.todo = todo
    }

}

class TodoItemViewHolder(
    private val binding: ListItemTodoBinding,
    private val viewModel: TodolistViewModel,
    private val navigateToTodo: NavigateToTodo
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var todo: Todo

    init {
        binding.root.setOnClickListener {
            navigateToTodo.navigate(todo)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            viewModel: TodolistViewModel,
            navigateToTodo: NavigateToTodo,
            notoColor: NotoColor
        ): TodoItemViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemTodoBinding.inflate(layoutInflater, parent, false)

            val colorPrimary = notoColor.getColorPrimary(binding.root.context)
            val colorOnPrimary = notoColor.getColorOnPrimary(binding.root.context)

            binding.check.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
            binding.star.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)

            return TodoItemViewHolder(binding, viewModel, navigateToTodo)
        }
    }

    fun bind(todo: Todo) {
        binding.todo = todo
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }
}

private class TodoItemDiffCallback : DiffUtil.ItemCallback<Todo>() {

    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.todoId == newItem.todoId
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }

}

interface NavigateToTodo {
    fun navigate(todo: Todo)
}