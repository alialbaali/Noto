package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.HeaderItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.header_item)
abstract class HeaderItem : EpoxyModelWithHolder<HeaderItem.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: Holder) = with(holder.binding) {
        tvTitle.text = title
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: HeaderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = HeaderItemBinding.bind(itemView)
        }
    }
}