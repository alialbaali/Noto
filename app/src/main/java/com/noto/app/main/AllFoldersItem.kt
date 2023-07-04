package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.AllFoldersItemBinding
import com.noto.app.util.*

private val StrokeWidth = 1.dp

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class AllFoldersItem : EpoxyModelWithHolder<AllFoldersItem.Holder>() {

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.isSelected = isSelected
        root.strokeWidth = StrokeWidth
        root.setOnClickListener(onClickListener)
        root.context?.let { context ->
            val colorResource = context.colorAttributeResource(R.attr.notoPrimaryColor)
            val backgroundColorResource = context.colorAttributeResource(R.attr.notoBackgroundColor)
            root.rippleColor = colorResource.toColorStateList()
            root.strokeColor = colorResource
            if (isSelected) {
                root.setCardBackgroundColor(colorResource.withDefaultAlpha())
                tvTitle.setTextColor(colorResource)
                ivIcon.imageTintList = colorResource.toColorStateList()
            } else {
                root.setCardBackgroundColor(backgroundColorResource)
                tvTitle.setTextColor(colorResource)
                ivIcon.imageTintList = colorResource.toColorStateList()
            }
        } ?: Unit
    }

    override fun getDefaultLayout(): Int = R.layout.all_folders_item

    class Holder : EpoxyHolder() {
        lateinit var binding: AllFoldersItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = AllFoldersItemBinding.bind(itemView)
        }
    }
}