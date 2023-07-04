package com.noto.app.settings.general

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.IconItemBinding
import com.noto.app.domain.model.Icon
import com.noto.app.util.*


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class IconItem : EpoxyModelWithHolder<IconItem.Holder>() {

    @EpoxyAttribute
    lateinit var icon: Icon

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val drawable = context.drawableResource(icon.toResource())?.mutate()
                ?.let { BitmapDrawable(root.resources, it.toBitmap(50.dp, 50.dp)) }
            root.text = context.stringResource(icon.toTitle())
            val selectedColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
            val color = context.colorAttributeResource(R.attr.notoBackgroundColor)
            root.background?.setTint(if (isSelected) selectedColor else color)
            root.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawable, null, null)
        }
        root.setOnClickListener(onClickListener)
    }

    override fun getDefaultLayout(): Int = R.layout.icon_item

    class Holder : EpoxyHolder() {
        lateinit var binding: IconItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = IconItemBinding.bind(itemView)
        }
    }
}
