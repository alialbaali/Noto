package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoteLabelItemBinding
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.colorAttributeResource
import com.noto.app.util.colorResource
import com.noto.app.util.toColorResourceId

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class NoteLabelItem : EpoxyModelWithHolder<NoteLabelItem.Holder>() {

    @EpoxyAttribute
    lateinit var label: Label

    @EpoxyAttribute
    lateinit var color: NotoColor

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            tvLabel.background?.mutate()?.setTint(context.colorResource(color.toColorResourceId()))
            tvLabel.setTextColor(context.colorAttributeResource(R.attr.notoBackgroundColor))
        }
        tvLabel.text = label.title
    }

    override fun getDefaultLayout(): Int = R.layout.note_label_item

    class Holder : EpoxyHolder() {
        lateinit var binding: NoteLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteLabelItemBinding.bind(itemView)
        }
    }
}