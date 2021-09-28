package com.noto.app.main

import android.annotation.SuppressLint
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
import com.noto.app.util.*

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
        val resources = root.resources
        val color = resources.colorResource(library.color.toResource())
        tvLibraryTitle.text = library.title
        tvLibraryNotesCount.text = resources.pluralsResource(R.plurals.notes_count, notesCount, notesCount).lowercase()
        ibDrag.isVisible = isManualSorting
        vColor.background.setTint(color)
        tvLibraryTitle.setTextColor(color)
        tvLibraryNotesCount.setTextColor(color)
        ibDrag.drawable?.mutate()?.setTint(color)
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        tvLibraryNotesCount.isVisible = isShowNotesCount
        tvLibraryTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(bottom = if (isShowNotesCount) 4.dp else 0.dp)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            root.outlineAmbientShadowColor = color
            root.outlineSpotShadowColor = color
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