package com.noto.note.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.database.SortMethod
import com.noto.databinding.ListItemNotebookBinding
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import timber.log.Timber

// Notebook List RV Adapter
internal class NotebookListRVAdapter(
    private val viewModel: NotebookListViewModel,
    private val navigateToNotebook: NavigateToNotebook
) :
    ListAdapter<Notebook, NotebookItemViewHolder>(NotebookItemDiffCallback()),
    NotebookItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookItemViewHolder {
        return NotebookItemViewHolder.create(parent, navigateToNotebook)
    }

    override fun onBindViewHolder(holder: NotebookItemViewHolder, position: Int) {
        val notebook = getItem(position)
        holder.notebook = notebook
        holder.bind(notebook)
    }

    override fun onMoveViewHolder(
        fromViewHolder: NotebookItemViewHolder, toViewHolder: NotebookItemViewHolder
    ) {

        val fromNotebook = currentList.find {
            it.notebookPosition == fromViewHolder.adapterPosition
        }!!

        val toNotebook = currentList.find {
            it.notebookPosition == toViewHolder.adapterPosition
        }!!

        Timber.i(fromNotebook.notebookPosition.toString())
        Timber.i(toNotebook.notebookPosition.toString())


        fromNotebook.notebookPosition = toNotebook.notebookPosition.also {
            toNotebook.notebookPosition = fromNotebook.notebookPosition
        }

        Timber.i(fromNotebook.notebookPosition.toString())
        Timber.i(toNotebook.notebookPosition.toString())


        fromViewHolder.drag(fromViewHolder.notebook)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
    }

    override fun onClearViewHolder(viewHolder: NotebookItemViewHolder) {
        viewHolder.clear(viewHolder.notebook)
        viewModel.updateSortMethod(SortMethod.Custom)
        viewModel.updateNotebooks(currentList)
    }
}

// Notebook Item ViewHolder
internal class NotebookItemViewHolder(
    private val binding: ListItemNotebookBinding,
    private val navigateToNotebook: NavigateToNotebook
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var notebook: Notebook

    private val colorPrimary by lazy {
        notebook.notoColor.getColorPrimary(binding.root.context)
    }

    private val colorOnPrimary by lazy {
        notebook.notoColor.getColorOnPrimary(binding.root.context)
    }

    private val resources = binding.root.resources

    private val drawable = resources.getDrawable(R.drawable.shape_notebook_item, null)

    private val rippleDrawable by lazy {
        RippleDrawable(
            ColorStateList.valueOf(colorOnPrimary),
            drawable,
            drawable
        )
    }

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
        binding.notebook = notebook
        binding.executePendingBindings()

        setDefaultBackground()
    }

    fun drag(notebook: Notebook) {
        binding.root.elevation = resources.getDimension(R.dimen.elevation_normal)
    }

    fun clear(notebook: Notebook) {
        binding.root.elevation = resources.getDimension(R.dimen.elevation_extra_small)

        setDefaultBackground()
    }

    private fun setDefaultBackground() {
        binding.root.background = rippleDrawable
        binding.root.backgroundTintList = ColorStateList.valueOf(colorPrimary)
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
