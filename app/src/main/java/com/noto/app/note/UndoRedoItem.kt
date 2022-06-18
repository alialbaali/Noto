package com.noto.app.note

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.UndoRedoItemBinding
import com.noto.app.util.colorAttributeResource


@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.undo_redo_item)
abstract class UndoRedoItem : EpoxyModelWithHolder<UndoRedoItem.Holder>() {

    @EpoxyAttribute
    lateinit var text: String

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onCopyClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        tvText.text = text
        root.context?.let { context ->
            val selectedColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
            val color = context.colorAttributeResource(R.attr.notoBackgroundColor)
            ll.background?.mutate()?.setTint(if (isSelected) selectedColor else color)
        }
        ll.setOnClickListener(onClickListener)
        ibCopy.setOnClickListener(onCopyClickListener)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: UndoRedoItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = UndoRedoItemBinding.bind(itemView)
        }
    }
}
