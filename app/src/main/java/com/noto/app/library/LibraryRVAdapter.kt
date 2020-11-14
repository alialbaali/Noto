package com.noto.app.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.BaseItemTouchHelperListener
import com.noto.app.R
import com.noto.app.databinding.ItemNotoBinding
import com.noto.app.noto.NotoViewModel
import com.noto.domain.model.Noto

class LibraryRVAdapter(
    private val listener: NotoItemClickListener,
) : ListAdapter<Noto, NotoItemViewHolder>(
    NoteItemDiffCallback()
), BaseItemTouchHelperListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotoItemViewHolder {
        return NotoItemViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: NotoItemViewHolder, position: Int) {
        val noto = getItem(position)
        holder.noto = noto
        holder.bind(noto)
    }

    override fun onMoveViewHolder(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder) {
        fromViewHolder as NotoItemViewHolder
        toViewHolder as NotoItemViewHolder

        val fromNoto = currentList.find { it.notoPosition == fromViewHolder.adapterPosition }!!
        val toNoto = currentList.find { it.notoPosition == fromViewHolder.adapterPosition }!!

//        fromNoto.notoPosition = toNoto.notoPosition.also { toNoto.notoPosition = fromNoto.notoPosition }

        fromViewHolder.drag(fromViewHolder.noto)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
    }

    override fun onSwipeViewHolder(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onClearViewHolder(viewHolder: RecyclerView.ViewHolder) {
        viewHolder as NotoItemViewHolder
        viewHolder.clear(viewHolder.noto)
//        viewModel.updateSortMethod(SortMethod.Custom)
    }

}

// Note Item ViewHolder
class NotoItemViewHolder(
    private val binding: ItemNotoBinding,
    private val listener: NotoItemClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var noto: Noto

    init {
        binding.root.setOnClickListener {
            listener.onClick(noto)
        }

        binding.root.setOnLongClickListener {
            listener.onLongClick(noto)
            true
        }

        binding.rbNotoStar.setOnClickListener {
            listener.toggleNotoStar(noto)
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
    fun bind(noto: Noto) {
        binding.tvNotoTitle.text = noto.notoTitle
        binding.tvNotoBody.text = noto.notoBody
        binding.rbNotoStar.isChecked = noto.notoIsStarred

//        if (noto.notoTitle.isBlank()) binding.tvNotoTitle.visibility = View.GONE
//        if (noto.notoBody.isBlank()) binding.tvNotoBody.visibility = View.GONE
//        if (noto.notoReminder != null) binding.ivNotoReminder.visibility = View.VISIBLE
    }

    fun drag(noto: Noto) {
        binding.root.elevation = binding.root.resources.getDimension(R.dimen.elevation_normal)
    }

    fun clear(noto: Noto) {
        binding.root.elevation = binding.root.resources.getDimension(R.dimen.elevation_extra_small)
    }
}

// Note Item Difference Callback
class NoteItemDiffCallback() : DiffUtil.ItemCallback<Noto>() {
    override fun areItemsTheSame(oldItem: Noto, newItem: Noto): Boolean {
        return oldItem.notoId == newItem.notoId
    }

    override fun areContentsTheSame(oldItem: Noto, newItem: Noto): Boolean {
        return oldItem == newItem
    }
}

interface NotoItemClickListener {
    fun onClick(noto: Noto)
    fun onLongClick(noto: Noto)
    fun toggleNotoStar(noto: Noto)
}