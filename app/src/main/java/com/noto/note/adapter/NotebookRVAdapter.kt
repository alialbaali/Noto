package com.noto.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.databinding.ListItemNoteBinding
import com.noto.note.model.Note

// Notebook RV Adapter
class NotebookRVAdapter(private val navigateToNote: NavigateToNote) : ListAdapter<Note, NoteItemViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder.create(parent, navigateToNote)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
        holder.id = note.noteId
    }

}

// Note Item ViewHolder
class NoteItemViewHolder(private val binding: ListItemNoteBinding, navigateToNote: NavigateToNote) :
    RecyclerView.ViewHolder(binding.root) {

    var id = 0L

    init {
        binding.root.setOnClickListener {
            navigateToNote.navigate(id)
        }
    }

    companion object {

        // Create ViewHolder Instance
        fun create(
            parent: ViewGroup,
            navigateToNote: NavigateToNote
        ): NoteItemViewHolder {
            return NoteItemViewHolder(
                ListItemNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), navigateToNote
            )
        }
    }

    // Bind note's values to the list item
    fun bind(note: Note) {
        binding.title.text = note.noteTitle
        binding.body.text = note.noteBody
    }
}

// Note Item Difference Callback
class NoteItemDiffCallback() : DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}

interface NavigateToNote {
    fun navigate(id: Long)
}