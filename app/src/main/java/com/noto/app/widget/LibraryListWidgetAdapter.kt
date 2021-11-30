package com.noto.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import com.noto.app.R
import com.noto.app.databinding.WidgetLibraryItemBinding
import com.noto.app.domain.model.Library
import com.noto.app.util.*

class LibraryListWidgetAdapter(
    context: Context,
    libraries: List<Library>,
    layoutResourceId: Int,
    private val isShowNotesCount: Boolean,
    private val countNotes: (Long) -> Int,
) : ArrayAdapter<Library>(context, layoutResourceId, libraries) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        return WidgetLibraryItemBinding.inflate(layoutInflater, parent, false).withBinding {
            getItem(position)?.let { library ->
                val color = context.colorResource(library.color.toResource())
                val notesCount = countNotes(library.id)
                tvLibraryNotesCount.text = context.pluralsResource(R.plurals.notes_count, notesCount, notesCount).lowercase()
                ivLibraryColor.setColorFilter(color)
                tvLibraryTitle.setTextColor(color)
                tvLibraryNotesCount.setTextColor(color)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    root.outlineAmbientShadowColor = color
                    root.outlineSpotShadowColor = color
                }
                tvLibraryTitle.text = library.title
                tvLibraryNotesCount.isVisible = isShowNotesCount
                tvLibraryTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMarginsRelative(bottom = if (isShowNotesCount) 4.dp else 0.dp)
                }
            }
        }
    }
}