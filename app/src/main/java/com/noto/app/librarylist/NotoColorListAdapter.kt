package com.noto.app.librarylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.databinding.NotoColorItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource

class NotoColorListAdapter(
    private val listener: NotoColorClickListener
) : ListAdapter<Pair<NotoColor, Boolean>, NotoColorListAdapter.NotoColorItemViewHolder>(NotoColorItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotoColorItemViewHolder = NotoColorItemViewHolder.create(parent, listener)

    override fun onBindViewHolder(holder: NotoColorItemViewHolder, position: Int) {
        val pair = getItem(position)
        holder.pair = pair
        holder.bind(pair)
    }

    class NotoColorItemViewHolder(val binding: NotoColorItemBinding, private val listener: NotoColorClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var pair: Pair<NotoColor, Boolean>

        init {
            binding.rb.setOnClickListener {
                listener.onClick(pair.first)
            }
        }

        companion object {
            fun create(parent: ViewGroup, listener: NotoColorClickListener): NotoColorItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NotoColorItemBinding.inflate(layoutInflater, parent, false)
                return NotoColorItemViewHolder(binding, listener)
            }
        }

        fun bind(pair: Pair<NotoColor, Boolean>) {
            binding.rb.backgroundTintList = binding.root.resources.colorStateResource(pair.first.toResource())
            binding.rb.isChecked = pair.second
        }
    }

    private class NotoColorItemDiffCallback : DiffUtil.ItemCallback<Pair<NotoColor, Boolean>>() {
        override fun areItemsTheSame(oldItem: Pair<NotoColor, Boolean>, newItem: Pair<NotoColor, Boolean>): Boolean = oldItem.first.ordinal == newItem.first.ordinal
        override fun areContentsTheSame(oldItem: Pair<NotoColor, Boolean>, newItem: Pair<NotoColor, Boolean>): Boolean = oldItem == newItem
    }

    fun interface NotoColorClickListener {
        fun onClick(notoColor: NotoColor)
    }
}