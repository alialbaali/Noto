package com.noto.app.util

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.HeaderItemBinding
import com.noto.app.domain.model.NotoColor

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.header_item)
abstract class HeaderItem : EpoxyModelWithHolder<HeaderItem.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    open var isVisible = false

    @EpoxyAttribute
    var color: NotoColor? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) = with(holder.binding) {
        tvTitle.text = title
        ibVisibility.animate().setDuration(DefaultAnimationDuration).rotation(if (isVisible) 180F else 0F)
        ibVisibility.setOnClickListener(onClickListener)
        ibVisibility.isVisible = onClickListener != null
        if (color != null) {
            val colorResource = root.context.colorResource(color!!.toResource())
            val colorStateList = colorResource.toColorStateList()
            tvTitle.setTextColor(colorResource)
            ibVisibility.imageTintList = colorStateList
            ibVisibility.background.setRippleColor(colorStateList)
        } else {
            val colorResource = root.context.colorAttributeResource(R.attr.notoSecondaryColor)
            val rippleColorResource = root.context.colorAttributeResource(R.attr.notoSurfaceColor)
            tvTitle.setTextColor(colorResource)
            ibVisibility.imageTintList = colorResource.toColorStateList()
            ibVisibility.background.setRippleColor(rippleColorResource.toColorStateList())
        }
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: HeaderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = HeaderItemBinding.bind(itemView)
        }
    }
}