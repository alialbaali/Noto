package com.noto.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.R
import com.noto.databinding.ListItemNotebookBinding
import com.noto.note.model.Notebook
import com.noto.note.model.NotebookColor

// Notebook List RV Adapter
internal class NotebookListRVAdapter(private val navigateToNotebook: NavigateToNotebook) :
    ListAdapter<Notebook, NotebookItemViewHolder>(NotebookItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookItemViewHolder {
        return NotebookItemViewHolder.create(parent, navigateToNotebook)
    }

    override fun onBindViewHolder(holder: NotebookItemViewHolder, position: Int) {
        val notebook = getItem(position)
        holder.bind(notebook)
        holder.notebook = notebook
    }

}

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

        when (notebook.notebookColor) {
            NotebookColor.GRAY -> {
                binding.cl.setBackgroundResource(R.drawable.notebook_item_background_gray_drawable)
            }
            NotebookColor.BLUE -> {
                binding.cl.setBackgroundResource(R.drawable.notebook_item_background_blue_drawable)
            }
            NotebookColor.PINK -> {
                binding.cl.setBackgroundResource(R.drawable.notebook_item_background_pink_drawable)
            }
            NotebookColor.CYAN -> {
                binding.cl.setBackgroundResource(R.drawable.notebook_item_background_cyan_drawable)
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
