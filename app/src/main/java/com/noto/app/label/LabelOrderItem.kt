package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LabelOrderItemBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class LabelOrderItem : EpoxyModelWithHolder<LabelOrderItem.Holder>() {

    @EpoxyAttribute
    lateinit var label: Label

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        tvLabel.text = label.title
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
    }

    override fun getDefaultLayout(): Int = R.layout.label_order_item

    class Holder : EpoxyHolder() {
        lateinit var binding: LabelOrderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LabelOrderItemBinding.bind(itemView)
        }
    }
}