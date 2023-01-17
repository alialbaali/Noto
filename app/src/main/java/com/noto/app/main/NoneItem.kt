package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoneItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.none_item)
abstract class NoneItem : EpoxyModelWithHolder<NoneItem.Holder>() {

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
        lateinit var binding: NoneItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoneItemBinding.bind(itemView)
        }
    }
}