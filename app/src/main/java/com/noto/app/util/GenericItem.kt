package com.noto.app.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import com.noto.app.databinding.GenericItemBinding

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.generic_item)
abstract class GenericItem : EpoxyModelWithHolder<GenericItem.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var icon: Drawable

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
        root.isSelected = isSelected
        tvTitle.text = title
        ivIcon.setImageDrawable(icon)
        ibDrag.visibility = if (isManualSorting) View.INVISIBLE else View.GONE
        root.setOnClickListener(onClickListener)
        tvNotesCount.isVisible = isShowNotesCount
        tvNotesCount.text = notesCount.toString()
        tvTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (!isShowNotesCount && !isManualSorting) 16.dp else 8.dp)
        }
        tvNotesCount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (isManualSorting) 8.dp else 0.dp)
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: GenericItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = GenericItemBinding.bind(itemView)
        }
    }
}