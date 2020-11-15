package com.noto.app.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.BaseItemTouchHelperListener
import com.noto.app.R
import com.noto.app.databinding.ItemNotoBinding
import com.noto.domain.model.Note

class LibraryRVAdapter(
    private val listener: NotoItemClickListener,
) : ListAdapter<Note, NotoItemViewHolder>(
    NoteItemDiffCallback()
), BaseItemTouchHelperListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotoItemViewHolder {
        return NotoItemViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: NotoItemViewHolder, position: Int) {
        val noto = getItem(position)
        holder.note = noto
        holder.bind(noto)
    }

    override fun onMoveViewHolder(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder) {
        fromViewHolder as NotoItemViewHolder
        toViewHolder as NotoItemViewHolder

        val fromNoto = currentList.find { it.position == fromViewHolder.adapterPosition }!!
        val toNoto = currentList.find { it.position == fromViewHolder.adapterPosition }!!

//        fromNoto.notoPosition = toNoto.notoPosition.also { toNoto.notoPosition = fromNoto.notoPosition }

        fromViewHolder.drag(fromViewHolder.note)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
    }

    override fun onSwipeViewHolder(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onClearViewHolder(viewHolder: RecyclerView.ViewHolder) {
        viewHolder as NotoItemViewHolder
        viewHolder.clear(viewHolder.note)
//        viewModel.updateSortMethod(SortMethod.Custom)
    }

}

// Note Item ViewHolder
class NotoItemViewHolder(
    private val binding: ItemNotoBinding,
    private val listener: NotoItemClickListener,
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

        binding.rbNotoStar.setOnClickListener {
            listener.toggleNotoStar(note)
        }
    }

    companion object {

        fun create(
            parent: ViewGroup,
            listener: NotoItemClickListener,
        ): NotoItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemNotoBinding.inflate(layoutInflater, parent, false)

            return NotoItemViewHolder(
                binding, listener
            )
        }
    }

    // Bind note's values to the list item
    fun bind(note: Note) {
        binding.tvNotoTitle.text = note.title
        binding.tvNotoBody.text = note.body
        binding.rbNotoStar.isChecked = note.isStarred

//        if (note.notoTitle.isBlank()) binding.tvNotoTitle.visibility = View.GONE
//        if (note.notoBody.isBlank()) binding.tvNotoBody.visibility = View.GONE
//        if (note.notoReminder != null) binding.ivNotoReminder.visibility = View.VISIBLE
    }

    fun drag(note: Note) {
        binding.root.elevation = binding.root.resources.getDimension(R.dimen.elevation_normal)
    }

    fun clear(note: Note) {
        binding.root.elevation = binding.root.resources.getDimension(R.dimen.elevation_extra_small)
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

interface NotoItemClickListener {
    fun onClick(note: Note)
    fun onLongClick(note: Note)
    fun toggleNotoStar(note: Note)
}