package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LibraryItemBinding
import com.noto.app.domain.model.Library
import com.noto.app.util.colorResource
import com.noto.app.util.stringResource
import com.noto.app.util.toCountText
import com.noto.app.util.toResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.library_item)
abstract class LibraryItem : EpoxyModelWithHolder<LibraryItem.Holder>() {

    @EpoxyAttribute
    lateinit var library: Library

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    var notesCount: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) {
        val resources = holder.binding.root.resources
        val color = resources.colorResource(library.color.toResource())
        holder.binding.tvLibraryTitle.text = library.title
        holder.binding.tvLibraryNotesCount.text = notesCount.toCountText(resources.stringResource(R.string.note), resources.stringResource(R.string.notes))
        holder.binding.ibDrag.isVisible = isManualSorting
        holder.binding.vColor.background.setTint(color)
        holder.binding.tvLibraryTitle.setTextColor(color)
        holder.binding.tvLibraryNotesCount.setTextColor(color)
        holder.binding.ibDrag.drawable?.setTint(color)
        holder.binding.ibDrag.setOnTouchListener(onDragHandleTouchListener)
        holder.binding.root.setOnClickListener(onClickListener)
        holder.binding.root.setOnLongClickListener(onLongClickListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            holder.binding.root.outlineAmbientShadowColor = color
            holder.binding.root.outlineSpotShadowColor = color
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