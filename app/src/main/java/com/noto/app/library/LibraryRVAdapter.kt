package com.noto.app.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.R
import com.noto.app.BaseItemTouchHelperListener
import com.noto.databinding.ItemNotoBinding

// Notebook RV Adapter
class NotoListRVAdapter(private val viewModel: LibraryViewModel, private val notoColor: NotoColor, private val listener: NotoClickListener) :

    ListAdapter<Noto, NotoItemViewHolder>(NoteItemDiffCallback()),
    BaseItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotoItemViewHolder {
        return NotoItemViewHolder.create(parent, viewModel, notoColor, listener)
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

        fromNoto.notoPosition = toNoto.notoPosition.also { toNoto.notoPosition = fromNoto.notoPosition }

        fromViewHolder.drag(fromViewHolder.noto)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
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
    private val viewModel: LibraryViewModel,
    private val notoColor: NotoColor,
    private val listener: NotoClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    lateinit var noto: Noto

    init {
        binding.root.setOnClickListener {
            listener.onClick(noto.notoId)
        }
    }

    companion object {

        // Create ViewHolder Instance
        fun create(
            parent: ViewGroup,
            viewModel: LibraryViewModel,
            notoColor: NotoColor,
            listener: NotoClickListener
        ): NotoItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemNotoBinding.inflate(layoutInflater, parent, false)

            return NotoItemViewHolder(
                binding, viewModel, notoColor, listener
            )
        }
    }

    // Bind note's values to the list item
    fun bind(noto: Noto) {
        binding.noto = noto
        binding.executePendingBindings()
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

interface NotoClickListener {
    fun onClick(id: Long)
}