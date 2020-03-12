package com.noto.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.databinding.NoteItemBinding
import com.noto.note.model.Note

// Notebook RV Adapter
class NotebookRVAdapter : ListAdapter<Note, NoteItemViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
        holder.id = note.id
    }

}

// Note Item ViewHolder
class NoteItemViewHolder(private val binding: NoteItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var id = 0L

    companion object {

        // Create ViewHolder Instance
        fun create(parent: ViewGroup): NoteItemViewHolder {
            return NoteItemViewHolder(
                NoteItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    // Bind note's values to the list item
    fun bind(note: Note) {
        binding.title.text = note.title
        binding.body.text = note.body
    }
}

// Note Item Difference Callback
class NoteItemDiffCallback() : DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}