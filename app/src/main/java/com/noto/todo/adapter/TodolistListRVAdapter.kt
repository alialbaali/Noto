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

internal class TodoListListRVAdapter(private val context: Context) :
    ListAdapter<Todolist, TodoListItemViewHolder>(TodoListItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListItemViewHolder {
        return TodoListItemViewHolder.create(parent, context)
    }

    override fun onBindViewHolder(holder: TodoListItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.item = item
    }


}

internal class TodoListItemViewHolder(
    private val binding: ListItemTodolistBinding,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {


    lateinit var item: Todolist

    companion object {
        fun create(parent: ViewGroup, context: Context): TodoListItemViewHolder {
            return TodoListItemViewHolder(
                ListItemTodolistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context
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

private class TodoListItemDiffCallback() : DiffUtil.ItemCallback<Todolist>() {
    override fun areItemsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem.todolistId == newItem.todolistId
    }

    override fun areContentsTheSame(oldItem: Todolist, newItem: Todolist): Boolean {
        return oldItem == newItem
    }
}