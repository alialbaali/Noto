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
import com.noto.app.databinding.NewLabelItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class NewLabelItem : EpoxyModelWithHolder<NewLabelItem.Holder>() {

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val resourceColor = context.colorResource(color.toResource())
            val colorStateList = resourceColor.toColorStateList()
            val backgroundColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            ibNewLabel.imageTintList = colorStateList
            ibNewLabel.background = context.drawableResource(R.drawable.label_item_shape)
                ?.mutate()
                ?.let { it as RippleDrawable }
                ?.let { it.getDrawable(0) as GradientDrawable }
                ?.apply {
                    setColor(backgroundColor)
                    setStroke(LabelDefaultStrokeWidth, resourceColor)
                }
                ?.toRippleDrawable(context)
                ?.also { it.setRippleColor(colorStateList) }
        }
        ibNewLabel.setOnClickListener(onClickListener)
    }

    override fun getDefaultLayout(): Int = R.layout.new_label_item

    class Holder : EpoxyHolder() {
        lateinit var binding: NewLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NewLabelItemBinding.bind(itemView)
        }
    }
}