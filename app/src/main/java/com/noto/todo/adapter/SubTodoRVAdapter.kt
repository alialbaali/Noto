package com.noto.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.databinding.ListItemSubTodoBinding
import com.noto.todo.model.SubTodo
import com.noto.todo.viewModel.TodoViewModel

class SubTodoRVAdapter(private val viewModel: TodoViewModel) :
    ListAdapter<SubTodo, SubTodoItemViewHolder>(SubTodoItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTodoItemViewHolder {
        return SubTodoItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SubTodoItemViewHolder, position: Int) {
        val subTodo = getItem(position)
        holder.bind(subTodo, viewModel)
    }
}

class SubTodoItemViewHolder(private val binding: ListItemSubTodoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): SubTodoItemViewHolder {
            return SubTodoItemViewHolder(
                ListItemSubTodoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    fun bind(subTodo: SubTodo, viewModel: TodoViewModel) {
        binding.subTodo = subTodo
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }
}

private class SubTodoItemDiffCallback() : DiffUtil.ItemCallback<SubTodo>() {

    override fun areItemsTheSame(oldItem: SubTodo, newItem: SubTodo): Boolean {
        return oldItem.subTodoId == newItem.subTodoId
    }

    override fun areContentsTheSame(oldItem: SubTodo, newItem: SubTodo): Boolean {
        return oldItem == newItem
    }
}