package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.PlaceholderItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.placeholder_item)
abstract class PlaceholderItem : EpoxyModelWithHolder<PlaceholderItem.Holder>() {

    @EpoxyAttribute
    lateinit var placeholder: String

    override fun bind(holder: Holder) = with(holder.binding) {
        tvPlaceholder.text = placeholder
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: PlaceholderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = PlaceholderItemBinding.bind(itemView)
        }
    }
}