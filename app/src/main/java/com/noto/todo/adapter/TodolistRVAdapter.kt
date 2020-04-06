package com.noto.todo.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.ListItemTodoBinding
import com.noto.todo.model.Todo

class TodolistRVAdapter(private val notoColor: NotoColor, private val navigateToTodo: NavigateToTodo) :
    ListAdapter<Todo, TodoItemViewHolder>(TodoItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        return TodoItemViewHolder.create(parent, notoColor, navigateToTodo)
    }

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
        holder.todo = todo
    }

}

class TodoItemViewHolder(
    private val binding: ListItemTodoBinding,
    private val notoColor: NotoColor,
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
            notoColor: NotoColor,
            navigateToTodo: NavigateToTodo
        ): TodoItemViewHolder {
            return TodoItemViewHolder(
                ListItemTodoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), notoColor, navigateToTodo
            )
        }
    }

    fun bind(todo: Todo) {
        binding.title.text = todo.todoTitle
//        binding.check.isChecked = todo.todoIsChecked
        binding.star.isChecked = todo.todoIsStared

        when (notoColor) {
            NotoColor.GRAY -> {
                binding.check.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryGray))
                binding.star.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryGray))
            }
            NotoColor.BLUE -> {
                binding.check.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryBlue))
                binding.star.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryBlue))
            }
            NotoColor.PINK -> {
                binding.check.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryPink))
                binding.star.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryPink))
            }
            NotoColor.CYAN -> {
                binding.check.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryCyan))
                binding.star.buttonTintList =
                    ColorStateList.valueOf(itemView.context.getColor(R.color.colorOnPrimaryCyan))
            }
        }
    }
}

private class TodoItemDiffCallback() : DiffUtil.ItemCallback<Todo>() {

    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.todoId == newItem.todoId
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.todoId == newItem.todoId
    }

}

interface NavigateToTodo {
    fun navigate(todo: Todo)
}