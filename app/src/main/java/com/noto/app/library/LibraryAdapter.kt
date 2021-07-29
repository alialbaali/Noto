package com.noto.app.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.Note

class LibraryAdapter(private val listener: NoteItemClickListener) : ListAdapter<Note, NoteItemViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val noto = getItem(position)
        holder.note = noto
        holder.bind(noto)
    }
}

class NoteItemViewHolder(
    private val binding: NoteItemBinding,
    private val listener: NoteItemClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var note: Note

    init {
        binding.root.setOnClickListener {
            listener.onClick(note)
        }

        binding.root.setOnLongClickListener {
            listener.onLongClick(note)
            true
        }

        binding.rbNoteStar.setOnClickListener {
            listener.toggleNotoStar(note)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            listener: NoteItemClickListener,
        ): NoteItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = NoteItemBinding.inflate(layoutInflater, parent, false)

            return NoteItemViewHolder(binding, listener)
        }
    }

    // Bind note's values to the list item
    fun bind(note: Note) {
        binding.tvNoteTitle.text = note.title
        binding.tvNoteBody.text = note.body
        binding.rbNoteStar.isChecked = note.isStarred

//        if (note.notoTitle.isBlank()) binding.tvNotoTitle.visibility = View.GONE
//        if (note.notoBody.isBlank()) binding.tvNotoBody.visibility = View.GONE
//        if (note.notoReminder != null) binding.ivNotoReminder.visibility = View.VISIBLE
    }

}

class NoteItemDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
}

interface NoteItemClickListener {
    fun onClick(note: Note)
    fun onLongClick(note: Note)
    fun toggleNotoStar(note: Note)
}
