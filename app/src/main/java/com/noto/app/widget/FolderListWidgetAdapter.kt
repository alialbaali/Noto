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
import com.noto.app.databinding.WidgetFolderItemBinding
import com.noto.app.domain.model.Folder
import com.noto.app.util.*

class FolderListWidgetAdapter(
    context: Context,
    folders: List<Pair<Folder, Int>>,
    layoutResourceId: Int,
    private val isShowNotesCount: Boolean,
) : ArrayAdapter<Pair<Folder, Int>>(context, layoutResourceId, folders) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        return WidgetFolderItemBinding.inflate(layoutInflater, parent, false).withBinding {
            getItem(position)?.let { entry ->
                val folder = entry.first
                val notesCount = entry.second
                val color = context.colorResource(folder.color.toColorResourceId())
                tvFolderNotesCount.text = notesCount.toString()
                tvFolderTitle.setTextColor(color)
                tvFolderNotesCount.setTextColor(color)
                ivFolderIcon.setColorFilter(color)
                tvFolderTitle.text = folder.getTitle(context)
                if (folder.isGeneral)
                    ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_general_24))
                else
                    ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_24))
                ivFolderIcon.imageTintList = color.toColorStateList()
                tvFolderNotesCount.isVisible = isShowNotesCount
                tvFolderTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMarginsRelative(end = if (isShowNotesCount) 8.dp else 16.dp)
                }
            }
        }
    }
}