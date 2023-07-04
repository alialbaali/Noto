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
@EpoxyModelClass
abstract class AllLabelItem : EpoxyModelWithHolder<AllLabelItem.Holder>() {

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val selectedBackgroundColor = context.colorResource(color.toResource())
            val selectedTextColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            val backgroundColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
            val textColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
            if (isSelected) {
                tvAllLabel.animateBackgroundColor(backgroundColor, selectedBackgroundColor)
                tvAllLabel.animateTextColor(textColor, selectedTextColor)
            } else {
                tvAllLabel.animateBackgroundColor(selectedBackgroundColor, backgroundColor)
                tvAllLabel.animateTextColor(selectedTextColor, textColor)
            }
        }
        tvAllLabel.setOnClickListener(onClickListener)
    }

    override fun getDefaultLayout(): Int = R.layout.all_label_item

    class Holder : EpoxyHolder() {
        lateinit var binding: AllLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = AllLabelItemBinding.bind(itemView)
        }
    }
}