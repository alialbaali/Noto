package com.noto.note.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.ListItemNotebookBinding
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel

// Notebook List RV Adapter
internal class NotebookListRVAdapter(
    private val navigateToNotebook: NavigateToNotebook
) :
    ListAdapter<Notebook, NotebookItemViewHolder>(NotebookItemDiffCallback()),
    NotebookItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookItemViewHolder {
        return NotebookItemViewHolder.create(parent, navigateToNotebook)
    }

    override fun onBindViewHolder(holder: NotebookItemViewHolder, position: Int) {
        val notebook = getItem(position)
        holder.bind(notebook)
        holder.notebook = notebook
    }

    override fun onMove(fromPosition: Int, toPosition: Int) {
//        TODO("Not yet implemented")
    }

    override fun onSwipe(position: Int) {
//        TODO("Not yet implemented")
    }

    override fun clearView() {
//        TODO("Not yet implemented")
    }

//    override fun onMove(fromPosition: Int, toPosition: Int) {
//        val notebook1 = getItem(fromPosition)
//        val notebook2 = getItem(toPosition)
//
//        Timber.i(notebook1.notebookPosition.toString())
//        Timber.i(notebook2.notebookPosition.toString())
//
//        notebook1.notebookPosition = notebook2.notebookPosition.also {
//            notebook2.notebookPosition = notebook1.notebookPosition
//        }
//
//        Timber.i(notebook1.notebookPosition.toString())
//        Timber.i(notebook2.notebookPosition.toString())
//
//
//        viewModel.viewModelScope.launch(Dispatchers.IO) {
//            viewModel.notebookRepository.updateNotebook(notebook1)
//            viewModel.notebookRepository.updateNotebook(notebook2)
//            viewModel.list = viewModel.notebookRepository.get()
//            Timber.i(viewModel.list.toString())
//        }
//
//
//
//        notifyItemMoved(fromPosition, toPosition)


//        notebookItemTouchHelper.onDrag(getItem(fromPosition), getItem(toPosition))
}

//    override fun onSwipe(position: Int) {
////        notebookItemTouchHelper.onSwipe(getItem(position))
//    }
//
//    override fun clearView() {
////        notebookItemTouchHelper.clearView()
//    }


// Notebook Item ViewHolder
internal class NotebookItemViewHolder(
    private val binding: ListItemNotebookBinding,
    private val navigateToNotebook: NavigateToNotebook
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var notebook: Notebook

    init {
        binding.root.let {

            it.setOnClickListener {
                navigateToNotebook.navigate(notebook)
            }

        }
    }

    companion object {

        // Create ViewHolder Instance
        fun create(
            parent: ViewGroup,
            navigateToNotebook: NavigateToNotebook
        ): NotebookItemViewHolder {
            return NotebookItemViewHolder(
                ListItemNotebookBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), navigateToNotebook
            )
        }
    }

    // Bind notebook's values to the list item
    fun bind(notebook: Notebook) {
        binding.notebookTv.text = notebook.notebookTitle
        val context = itemView.context
        val ripple = RippleDrawable(
            ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryGray)),
            context.getDrawable(R.drawable.shape_notebook_item),
            context.getDrawable(R.drawable.shape_notebook_item)
        )

        when (notebook.notoColor) {
            NotoColor.GRAY -> {
                ripple.setTint(context.getColor(R.color.colorPrimaryGray))
                ripple.setColor(ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryGray)))
                binding.root.background = ripple
            }
            NotoColor.BLUE -> {
                ripple.setTint(context.getColor(R.color.colorPrimaryBlue))
                ripple.setColor(ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryBlue)))
                binding.root.background = ripple
            }
            NotoColor.PINK -> {
                ripple.setTint(context.getColor(R.color.colorPrimaryPink))
                ripple.setColor(ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryPink)))
                binding.root.background = ripple
            }
            NotoColor.CYAN -> {
                ripple.setTint(context.getColor(R.color.colorPrimaryCyan))
                ripple.setColor(ColorStateList.valueOf(context.getColor(R.color.colorOnPrimaryCyan)))
                binding.root.background = ripple
            }
        }
    }

}

// Notebook Item Difference Callback
private class NotebookItemDiffCallback() : DiffUtil.ItemCallback<Notebook>() {
    override fun areItemsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
        return oldItem.notebookId == newItem.notebookId
    }

    override fun areContentsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
        return oldItem == newItem
    }
}

internal interface NavigateToNotebook {
    fun navigate(notebook: Notebook)
}

//internal interface NotebookItemTouchHelper {
//
//    fun onDrag(notebook: Notebook, target: Notebook)
//
//    fun onSwipe(notebook: Notebook)
//
//    fun clearView()
//}
