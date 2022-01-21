package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoParentItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.no_parent_item)
abstract class NoParentItem : EpoxyModelWithHolder<NoParentItem.Holder>() {

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.isSelected = isSelected
        root.setOnClickListener(onClickListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NoParentItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoParentItemBinding.bind(itemView)
        }
    }
}