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

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onLongClickListener: View.OnLongClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onCreateClickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) = with(holder.binding) {
        tvTitle.text = title
        ibVisibility.animate().setDuration(DefaultAnimationDuration).rotation(if (isVisible) 180F else 0F)
        ibVisibility.contentDescription = root.context?.stringResource(if (isVisible) R.string.hide else R.string.show)
        ibVisibility.setOnClickListener(onClickListener)
        ibCreate.setOnClickListener(onCreateClickListener)
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        root.isClickable = onClickListener != null
        ibVisibility.isVisible = onClickListener != null
        ibCreate.isVisible = onCreateClickListener != null
        if (color != null) {
            val colorResource = root.context.colorResource(color!!.toResource())
            val colorStateList = colorResource.toColorStateList()
            tvTitle.setTextColor(colorResource)
            ibVisibility.imageTintList = colorStateList
            ibCreate.imageTintList = colorStateList
            root.background.setRippleColor(colorStateList)
            ibCreate.background.setRippleColor(colorStateList)
            ibVisibility.background.setRippleColor(colorStateList)
        } else {
            val colorResource = root.context.colorAttributeResource(R.attr.notoSecondaryColor)
            tvTitle.setTextColor(colorResource)
            ibVisibility.imageTintList = colorResource.toColorStateList()
            ibCreate.imageTintList = colorResource.toColorStateList()
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