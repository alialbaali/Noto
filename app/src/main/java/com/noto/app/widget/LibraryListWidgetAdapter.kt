package com.noto.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import com.noto.app.R
import com.noto.app.databinding.WidgetLibraryItemBinding
import com.noto.app.domain.model.Folder
import com.noto.app.util.*

class LibraryListWidgetAdapter(
    context: Context,
    libraries: List<Pair<Folder, Int>>,
    layoutResourceId: Int,
    private val isShowNotesCount: Boolean,
) : ArrayAdapter<Pair<Folder, Int>>(context, layoutResourceId, libraries) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        return WidgetLibraryItemBinding.inflate(layoutInflater, parent, false).withBinding {
            getItem(position)?.let { entry ->
                val library = entry.first
                val notesCount = entry.second
                val color = context.colorResource(library.color.toResource())
                tvLibraryNotesCount.text = notesCount.toString()
                tvLibraryTitle.setTextColor(color)
                tvLibraryNotesCount.setTextColor(color)
                ivLibraryIcon.setColorFilter(color)
                tvLibraryTitle.text = library.getTitle(context)
                if (library.isInbox)
                    ivLibraryIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_inbox_24))
                else
                    ivLibraryIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_24))
                ivLibraryIcon.imageTintList = color.toColorStateList()
                tvLibraryNotesCount.isVisible = isShowNotesCount
                tvLibraryTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMarginsRelative(end = if (isShowNotesCount) 8.dp else 16.dp)
                }
            }
        }
    }
}