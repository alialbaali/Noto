package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Build
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
import com.noto.app.databinding.LibraryItemBinding
import com.noto.app.domain.model.Library
import com.noto.app.util.colorResource
import com.noto.app.util.dp
import com.noto.app.util.pluralsResource
import com.noto.app.util.toResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.library_item)
abstract class LibraryItem : EpoxyModelWithHolder<LibraryItem.Holder>() {

    @EpoxyAttribute
    lateinit var library: Library

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    open var isShowNotesCount: Boolean = true

    @EpoxyAttribute
    var notesCount: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            tvLibraryNotesCount.text = context.pluralsResource(R.plurals.notes_count, notesCount, notesCount).lowercase()
            ivLibraryColor.setColorFilter(color)
            tvLibraryTitle.setTextColor(color)
            tvLibraryNotesCount.setTextColor(color)
            ibDrag.drawable?.mutate()?.setTint(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                root.outlineAmbientShadowColor = color
                root.outlineSpotShadowColor = color
            }
        }
        tvLibraryTitle.text = library.title
        ibDrag.isVisible = isManualSorting
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        tvLibraryNotesCount.isVisible = isShowNotesCount
        tvLibraryTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(bottom = if (isShowNotesCount) 4.dp else 0.dp)
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LibraryItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LibraryItemBinding.bind(itemView)
        }
    }
}