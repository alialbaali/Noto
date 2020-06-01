package com.noto.label

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.domain.model.Label
import com.noto.R
import com.noto.databinding.ItemLabelBinding
import com.noto.util.getValue

class LabelListRVAdapter(private val viewModel: LabelListViewModel) : ListAdapter<Label, LabelListRVAdapter.LabelItemViewHolder>(LabelItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelItemViewHolder {
        return LabelItemViewHolder.create(parent, viewModel)
    }

    override fun onBindViewHolder(holder: LabelItemViewHolder, position: Int) {
        val label = getItem(position)
        holder.label = label
        holder.bind(label)
    }

    class LabelItemViewHolder(private val binding: ItemLabelBinding, private val viewModel: LabelListViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var label: Label

        private val resources = binding.root.resources

        private val drawable = resources.getDrawable(R.drawable.ripple, null)

        private val rippleDrawable by lazy {
            RippleDrawable(ColorStateList.valueOf(resources.getColor(label.notoColor.getValue())), drawable, drawable)
        }

        init {
            binding.root.setOnClickListener {
                LabelDialog(binding.root.context, viewModel, label)
            }
        }

        companion object {
            fun create(parent: ViewGroup, viewModel: LabelListViewModel): LabelItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLabelBinding.inflate(layoutInflater, parent, false)

                return LabelItemViewHolder(binding, viewModel)
            }
        }

        fun bind(label: Label) {
            binding.label = label
            binding.ivLabelNotoColor.imageTintList = binding.root.resources.getColorStateList(label.notoColor.getValue())
            binding.root.background = rippleDrawable
            binding.root.backgroundTintList = binding.root.resources.getColorStateList(label.notoColor.getValue())
            binding.executePendingBindings()
        }
    }

    class LabelItemDiffCallback() : DiffUtil.ItemCallback<Label>() {
        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem.labelId == newItem.labelId
        }

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem == newItem
        }
    }
}