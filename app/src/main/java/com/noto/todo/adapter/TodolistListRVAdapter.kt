package com.noto.todo.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.database.SortMethod
import com.noto.databinding.ListItemTodolistBinding
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary

internal class TodolistListRVAdapter(private val viewModel: TodolistListViewModel, private val navigateToTodolist: NavigateToTodolist) :
    ListAdapter<Todolist, TodolistItemViewHolder>(TodolistItemDiffCallback()), TodoListItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodolistItemViewHolder {
        return TodolistItemViewHolder.create(parent, navigateToTodolist)
    }

    override fun onBindViewHolder(holder: TodolistItemViewHolder, position: Int) {
        val todolist = getItem(position)
        holder.todolist = todolist
        holder.bind(todolist, viewModel)
    }

    override fun onMoveViewHolder(fromViewHolder: TodolistItemViewHolder, toViewHolder: TodolistItemViewHolder) {

        val fromTodolist = currentList.find { it.todolistPosition == fromViewHolder.adapterPosition }!!

        val toTodolist = currentList.find { it.todolistPosition == toViewHolder.adapterPosition }!!

        fromTodolist.todolistPosition = toTodolist.todolistPosition.also { toTodolist.todolistPosition = fromTodolist.todolistPosition }

        fromViewHolder.drag(fromViewHolder.todolist)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
    }

    override fun onClearViewHolder(viewHolder: TodolistItemViewHolder) {
        viewHolder.clear(viewHolder.todolist)
        viewModel.updateSortMethod(SortMethod.Custom)
        viewModel.updateTodolists(currentList)
    }

}

internal class TodolistItemViewHolder(
    private val binding: ListItemTodolistBinding,
    private val navigateToTodolist: NavigateToTodolist
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var todolist: Todolist

    private val colorPrimary by lazy {
        todolist.notoColor.getColorPrimary(binding.root.context)
    }

    private val colorOnPrimary by lazy {
        todolist.notoColor.getColorOnPrimary(binding.root.context)
    }

    private val resources = binding.root.resources

    private val drawable = binding.root.resources.getDrawable(R.drawable.shape_todolist_item, null)

    private val rippleDrawable by lazy { RippleDrawable(ColorStateList.valueOf(colorPrimary), drawable, drawable) }

    init {
        binding.root.setOnClickListener {
            navigateToTodolist.navigate(todolist)
        }
    }

    companion object {
        fun create(parent: ViewGroup, navigateToTodolist: NavigateToTodolist): TodolistItemViewHolder {

            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemTodolistBinding.inflate(layoutInflater, parent, false)

            return TodolistItemViewHolder(binding, navigateToTodolist)
        }
    }

    fun bind(todolist: Todolist, viewModel: TodolistListViewModel) {
        binding.todolist = todolist
        binding.executePendingBindings()

        binding.todosCount.text = viewModel.countTodos(todolist.todolistId).toString().plus(" Todos")
        binding.todosCount.setTextColor(colorOnPrimary)
        binding.todoListTv.compoundDrawableTintList = ColorStateList.valueOf(colorOnPrimary)
        binding.todolistIcon.imageTintList = ColorStateList.valueOf(colorOnPrimary)
        binding.todoListTv.setTextColor(colorOnPrimary)
        binding.root.background = rippleDrawable
    }

    fun drag(todolist: Todolist) {
        binding.root.elevation = resources.getDimension(R.dimen.elevation_normal)
    }

    fun clear(todolist: Todolist) {
        binding.root.elevation = resources.getDimension(R.dimen.elevation_extra_small)
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