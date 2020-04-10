package com.noto.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.database.NotoColor
import com.noto.databinding.ListItemNoteBinding
import com.noto.note.model.Note
import com.noto.note.viewModel.NotebookViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary

// Notebook RV Adapter
class NotebookRVAdapter(
    private val viewModel: NotebookViewModel,
    private val notoColor: NotoColor,
    private val navigateToNote: NavigateToNote
) :
    ListAdapter<Note, NoteItemViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder.create(parent, viewModel, notoColor, navigateToNote)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val note = getItem(position)
        holder.note = note
        holder.bind(note)
    }

}

// Note Item ViewHolder
class NoteItemViewHolder(
    private val binding: ListItemNoteBinding,
    private val viewModel: NotebookViewModel,
    private val notoColor: NotoColor,
    private val navigateToNote: NavigateToNote
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var note: Note

    private val colorPrimary = notoColor.getColorPrimary(binding.root.context)

    private val colorOnPrimary = notoColor.getColorOnPrimary(binding.root.context)

    init {
        binding.root.setOnClickListener {
            navigateToNote.navigate(note.noteId)
        }
    }

    companion object {

        // Create ViewHolder Instance
        fun create(
            parent: ViewGroup,
            viewModel: NotebookViewModel,
            notoColor: NotoColor,
            navigateToNote: NavigateToNote
        ): NoteItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemNoteBinding.inflate(layoutInflater, parent, false)

            return NoteItemViewHolder(
                binding, viewModel, notoColor, navigateToNote
            )
        }
    }

    // Bind note's values to the list item
    fun bind(note: Note) {
        binding.note = note
        binding.executePendingBindings()
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