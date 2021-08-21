package com.noto.app.library

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
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.noto_color_item)
abstract class NotoColorItem : EpoxyModelWithHolder<NotoColorItem.Holder>() {

    @EpoxyAttribute
    lateinit var notoColor: NotoColor

    @EpoxyAttribute
    open var isChecked: Boolean = false

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) {
        holder.binding.rb.backgroundTintList = holder.binding.root.resources.colorStateResource(notoColor.toResource())
        holder.binding.rb.isChecked = isChecked
        holder.binding.rb.setOnClickListener(onClickListener)
        TooltipCompat.setTooltipText(holder.binding.rb, notoColor.name)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NotoColorItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NotoColorItemBinding.bind(itemView)
        }
    }
}