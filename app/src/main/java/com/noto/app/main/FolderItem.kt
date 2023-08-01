package com.noto.app.main

import android.annotation.SuppressLint
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePaddingRelative
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.FolderItemBinding
import com.noto.app.domain.model.Folder
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class FolderItem : EpoxyModelWithHolder<FolderItem.Holder>() {

    @EpoxyAttribute
    lateinit var folder: Folder

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    open var isShowNotesCount: Boolean = true

    @EpoxyAttribute
    var notesCount: Int = 0

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    var depth: Int = 1

    @EpoxyAttribute
    open var isEnabled: Boolean = true

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val defaultColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            val color = context.colorResource(folder.color.toResource())
            val selectedColor = color.withDefaultAlpha()
            val backgroundDrawable = root.background?.mutate()
            val rippleDrawable = backgroundDrawable as? RippleDrawable
            if (isEnabled) {
                if (isSelected) {
                    backgroundDrawable?.setTint(selectedColor)
                    rippleDrawable?.setColor(color.toColorStateList())
                } else {
                    backgroundDrawable?.setTint(defaultColor)
                    rippleDrawable?.setColor(selectedColor.toColorStateList())
                }
                root.alpha = 1.0F
                root.isEnabled = true
            } else {
                backgroundDrawable?.setTint(defaultColor)
                root.alpha = 0.5F
                root.isEnabled = false
            }
            tvFolderNotesCount.text = notesCount.toString()
            tvFolderTitle.setTextColor(color)
            tvFolderNotesCount.setTextColor(color)
            ibFolderHandle.drawable?.mutate()?.setTint(color)
            tvFolderTitle.text = folder.getTitle(context)
            ivFolderIcon.contentDescription = folder.getTitle(context)
            if (isSelected) {
                ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_open_24))
            } else {
                if (folder.isGeneral) {
                    ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_general_24))
                } else if (folder.isPinned) {
                    ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_pinned_folder_24))
                } else {
                    ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_24))
                }
            }
            ivFolderIcon.imageTintList = color.toColorStateList()
        }
        ibFolderHandle.visibility = when {
            isManualSorting && !folder.isGeneral -> View.VISIBLE
            isManualSorting && folder.isGeneral -> View.INVISIBLE
            else -> View.GONE
        }
        ibFolderHandle.setOnTouchListener(onDragHandleTouchListener)
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        tvFolderNotesCount.isVisible = isShowNotesCount
        root.updatePaddingRelative(depth * 16.dp)
        tvFolderTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (!isShowNotesCount && !isManualSorting) 16.dp else 8.dp)
        }
        tvFolderNotesCount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(end = if (isManualSorting) 8.dp else 0.dp)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.folder_item

    class Holder : EpoxyHolder() {
        lateinit var binding: FolderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = FolderItemBinding.bind(itemView)
        }
    }
}