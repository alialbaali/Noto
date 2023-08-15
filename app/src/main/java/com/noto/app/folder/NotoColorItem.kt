package com.noto.app.folder

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NotoColorItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.colorResource
import com.noto.app.util.toColorStateList
import com.noto.app.util.toColorResourceId

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class NotoColorItem : EpoxyModelWithHolder<NotoColorItem.Holder>() {

    @EpoxyAttribute
    lateinit var notoColor: NotoColor

    @EpoxyAttribute
    open var isChecked: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            rb.backgroundTintList = context.colorResource(notoColor.toColorResourceId()).toColorStateList()
        }
        rb.isChecked = isChecked
        rb.setOnClickListener(onClickListener)
        TooltipCompat.setTooltipText(rb, notoColor.name)
    }

    override fun getDefaultLayout(): Int = R.layout.noto_color_item

    class Holder : EpoxyHolder() {
        lateinit var binding: NotoColorItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NotoColorItemBinding.bind(itemView)
        }
    }
}