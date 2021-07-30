package com.noto.app.notelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.Note

class NoteListAdapter(private val listener: NoteItemClickListener) : ListAdapter<Note, NoteListAdapter.NoteItemViewHolder>(NoteItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        return NoteItemViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        val note = getItem(position)
        holder.note = note
        holder.bind(note)
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

        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteBody.text = note.body
            binding.rbNoteStar.isChecked = note.isStarred
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
}


