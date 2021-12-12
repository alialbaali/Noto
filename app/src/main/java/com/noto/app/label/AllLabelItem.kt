package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.AllLabelItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.all_label_item)
abstract class AllLabelItem : EpoxyModelWithHolder<AllLabelItem.Holder>() {

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val backgroundColor = context.attributeColoResource(R.attr.notoBackgroundColor)
            val resourceColor = context.colorResource(color.toResource())
            if (isSelected) {
                tvAllLabel.animateBackgroundColor(backgroundColor, resourceColor)
                tvAllLabel.animateTextColor(resourceColor, backgroundColor)
            } else {
                tvAllLabel.animateLabelColors(fromColor = resourceColor, toColor = backgroundColor)
                tvAllLabel.animateTextColor(backgroundColor, resourceColor)
            }
        }
        tvAllLabel.setOnClickListener(onClickListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: AllLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = AllLabelItemBinding.bind(itemView)
        }
    }
}