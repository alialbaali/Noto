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
@EpoxyModelClass
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

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val selectedBackgroundColor = context.colorResource(color.toColorResourceId())
            val selectedTextColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            val backgroundColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
            val textColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
            if (isSelected) {
                tvLabel.animateBackgroundColor(backgroundColor, selectedBackgroundColor)
                tvLabel.animateTextColor(textColor, selectedTextColor)
            } else {
                tvLabel.animateBackgroundColor(selectedBackgroundColor, backgroundColor)
                tvLabel.animateTextColor(selectedTextColor, textColor)
            }
        }
        tvLabel.text = label.title
        tvLabel.setOnClickListener(onClickListener)
        tvLabel.setOnLongClickListener(onLongClickListener)
    }

    override fun getDefaultLayout(): Int = R.layout.label_item

    class Holder : EpoxyHolder() {
        lateinit var binding: LabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LabelItemBinding.bind(itemView)
        }
    }
}