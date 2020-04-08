package com.noto.todo.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.database.NotoColor
import com.noto.databinding.ListItemSubTodoBinding
import com.noto.todo.model.SubTodo
import com.noto.todo.viewModel.TodoViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary

class SubTodoRVAdapter(private val viewModel: TodoViewModel, private val notoColor: NotoColor) :
    ListAdapter<SubTodo, SubTodoItemViewHolder>(SubTodoItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTodoItemViewHolder {
        return SubTodoItemViewHolder.create(parent, notoColor)
    }

    override fun onBindViewHolder(holder: SubTodoItemViewHolder, position: Int) {
        val subTodo = getItem(position)
        holder.bind(subTodo, viewModel)
    }

}

class SubTodoItemViewHolder(private val binding: ListItemSubTodoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, notoColor: NotoColor): SubTodoItemViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemSubTodoBinding.inflate(layoutInflater, parent, false)

            val colorPrimary = notoColor.getColorPrimary(binding.root.context)
            val colorOnPrimary = notoColor.getColorOnPrimary(binding.root.context)

            binding.check.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
            binding.delete.imageTintList = ColorStateList.valueOf(colorOnPrimary)

            return SubTodoItemViewHolder(binding)
        }
    }

    fun bind(subTodo: SubTodo, viewModel: TodoViewModel) {
        binding.subTodo = subTodo
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }
}

private class SubTodoItemDiffCallback : DiffUtil.ItemCallback<SubTodo>() {

    override fun areItemsTheSame(oldItem: SubTodo, newItem: SubTodo): Boolean {
        return oldItem.subTodoId == newItem.subTodoId
    }

    override fun areContentsTheSame(oldItem: SubTodo, newItem: SubTodo): Boolean {
        return oldItem == newItem
    }

}