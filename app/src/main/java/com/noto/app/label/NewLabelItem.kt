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
@EpoxyModelClass(layout = R.layout.new_label_item)
abstract class NewLabelItem : EpoxyModelWithHolder<NewLabelItem.Holder>() {

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val resourceColor = context.colorResource(color.toResource())
            ibNewLabel.imageTintList = resourceColor.toColorStateList()
            ibNewLabel.background = context.drawableResource(R.drawable.label_item_shape)
                ?.mutate()
                ?.let { it as RippleDrawable }
                ?.let { it.getDrawable(0) as GradientDrawable }
                ?.apply {
                    setStroke(LabelDefaultStrokeWidth, resourceColor)
                    cornerRadius = LabelDefaultCornerRadius
                }
                ?.toRippleDrawable(context)
        }
        ibNewLabel.setOnClickListener(onClickListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NewLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NewLabelItemBinding.bind(itemView)
        }
    }
}