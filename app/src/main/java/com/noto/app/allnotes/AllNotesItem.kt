package com.noto.app.allnotes

import android.annotation.SuppressLint
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.AllNotesItemBinding
import com.noto.app.util.attributeColoResource
import com.noto.app.util.dp
import com.noto.app.util.toColorStateList
import com.noto.app.util.withDefaultAlpha

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.all_notes_item)
abstract class AllNotesItem : EpoxyModelWithHolder<AllNotesItem.Holder>() {

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    open var isShowNotesCount: Boolean = true

    @EpoxyAttribute
    var notesCount: Int = 0

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val color = context.attributeColoResource(R.attr.notoPrimaryColor)
            val selectedColorStateList = color.withDefaultAlpha().toColorStateList()
            val rippleDrawable = root.background as RippleDrawable
            rippleDrawable.setColor(selectedColorStateList)
            tvNotesCount.text = notesCount.toString()
            tvTitle.setTextColor(color)
            tvNotesCount.setTextColor(color)
            root.backgroundTintList = if (isSelected)
                selectedColorStateList
            else
                context.attributeColoResource(R.attr.notoBackgroundColor).toColorStateList()
        }
        ibDrag.visibility = if (isManualSorting) View.INVISIBLE else View.GONE
        root.setOnClickListener(onClickListener)
        tvNotesCount.isVisible = isShowNotesCount
        tvTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (!isShowNotesCount && !isManualSorting) 16.dp else 8.dp)
        }
        tvNotesCount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (isManualSorting) 8.dp else 0.dp)
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: AllNotesItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = AllNotesItemBinding.bind(itemView)
        }
    }
}