package com.noto.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.databinding.NotebookItemBinding
import com.noto.note.model.Notebook

// Notebook List RV Adapter
internal class NotebookListRVAdapter :
    ListAdapter<Notebook, NotebookItemViewHolder>(NotebookItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookItemViewHolder {
        return NotebookItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotebookItemViewHolder, position: Int) {
        val notebook = getItem(position)
        holder.bind(notebook)
        holder.id = notebook.id
    }

}

// Notebook Item ViewHolder
internal class NotebookItemViewHolder(private val binding: NotebookItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var id = 0L

    init {
        binding.root.let {

            it.setOnClickListener {
                TODO("Implement a callback that responds to clicks")
            }

        }
    }

    companion object {

        // Create ViewHolder Instance
        fun create(parent: ViewGroup): NotebookItemViewHolder {
            return NotebookItemViewHolder(
                NotebookItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    // Bind notebook's values to the list item
    fun bind(notebook: Notebook) {
        binding.notebookTv.text = notebook.title
    }

}

// Notebook Item Difference Callback
private class NotebookItemDiffCallback() : DiffUtil.ItemCallback<Notebook>() {
    override fun areItemsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
        return oldItem == newItem
    }
}

