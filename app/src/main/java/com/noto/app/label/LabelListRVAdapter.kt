package com.noto.app.label

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.databinding.ItemLabelBinding
import com.noto.domain.model.Label
import com.noto.app.util.toResource

class LabelListRVAdapter(private val labelItemListener: LabelItemListener) : ListAdapter<Label, LabelListRVAdapter.LabelItemViewHolder>(
    LabelItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelItemViewHolder {
        return LabelItemViewHolder.create(parent, labelItemListener)
    }

    override fun onBindViewHolder(holder: LabelItemViewHolder, position: Int) {
        val label = getItem(position)
        holder.label = label
        holder.bind(label)
    }

    class LabelItemViewHolder(private val binding: ItemLabelBinding, labelItemListener: LabelItemListener) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var label: Label

        private val resources = binding.root.resources

        private val drawable = resources.getDrawable(R.drawable.ripple, null)

        private val rippleDrawable by lazy {
            RippleDrawable(ColorStateList.valueOf(resources.getColor(label.labelColor.toResource())), drawable, drawable)
        }

        init {
            binding.root.setOnClickListener {
                labelItemListener.onClick(label)
            }
        }

        companion object {
            fun create(parent: ViewGroup, labelItemListener: LabelItemListener): LabelItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ItemLabelBinding.inflate(layoutInflater, parent, false)

                return LabelItemViewHolder(binding, labelItemListener)
            }
        }

        fun bind(label: Label) {
            binding.label = label
            binding.executePendingBindings()
            binding.ivLabelNotoColor.imageTintList = ResourcesCompat.getColorStateList(resources, label.labelColor.toResource(), null)
            binding.root.background = rippleDrawable
            binding.root.backgroundTintList = ResourcesCompat.getColorStateList(resources, label.labelColor.toResource(), null)
        }
    }

    class LabelItemDiffCallback() : DiffUtil.ItemCallback<Label>() {

        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean = oldItem.labelId == newItem.labelId

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean = oldItem == newItem

    }

    interface LabelItemListener {

        fun onClick(label: Label)

    }

}