package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LabelItemBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.label_item)
abstract class LabelItem : EpoxyModelWithHolder<LabelItem.Holder>() {

    @EpoxyAttribute
    lateinit var label: Label

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute
    var backgroundColor: Int = 0

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val resourceColor = context.colorResource(color.toResource())
            val backgroundColor = if (backgroundColor == 0) context.attributeColoResource(R.attr.notoBackgroundColor) else backgroundColor
            if (isSelected) {
                tvLabel.animateBackgroundColor(backgroundColor, resourceColor)
                tvLabel.animateTextColor(resourceColor, backgroundColor)
            } else {
                tvLabel.animateLabelColors(fromColor = resourceColor, toColor = backgroundColor)
                tvLabel.animateTextColor(backgroundColor, resourceColor)
            }
        }
        tvLabel.text = label.title
        tvLabel.setOnClickListener(onClickListener)
        tvLabel.setOnLongClickListener(onLongClickListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LabelItemBinding.bind(itemView)
        }
    }
}