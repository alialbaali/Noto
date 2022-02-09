package com.noto.app.label

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LabelOrderItemBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.label_order_item)
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
        root.context?.let { context ->
            val resourceColor = context.colorResource(color.toResource())
            val backgroundColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            tvLabel.background = root.context.drawableResource(R.drawable.label_item_shape)
                ?.mutate()
                ?.let { it as RippleDrawable }
                ?.let { it.getDrawable(0) as GradientDrawable }
                ?.apply {
                    setStroke(LabelDefaultStrokeWidth, resourceColor)
                    cornerRadius = LabelDefaultCornerRadius
                    setColor(backgroundColor)
                }
                ?.toRippleDrawable(context)
            tvLabel.setTextColor(resourceColor)
            ibDrag.imageTintList = resourceColor.toColorStateList()
        }
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LabelOrderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LabelOrderItemBinding.bind(itemView)
        }
    }
}