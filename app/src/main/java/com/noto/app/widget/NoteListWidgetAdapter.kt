package com.noto.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.noto.app.R
import com.noto.app.databinding.NoteLabelItemBinding
import com.noto.app.databinding.WidgetNoteItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

class NoteListWidgetAdapter(
    context: Context,
    layoutResourceId: Int,
    notes: List<NoteWithLabels>,
    private val isShowCreationDate: Boolean,
    private val color: NotoColor,
    private val previewSize: Int,
) : ArrayAdapter<NoteWithLabels>(context, layoutResourceId, notes) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        return WidgetNoteItemBinding.inflate(layoutInflater, parent, false).withBinding {
            getItem(position)?.let { pair ->
                val note = pair.first
                val labels = pair.second
                root.context?.let { context ->
                    val colorResource = context.colorResource(color.toResource())
                    tvNoteTitle.setLinkTextColor(colorResource)
                    tvNoteBody.setLinkTextColor(colorResource)
                    if (isShowCreationDate)
                        tvCreationDate.text = context.stringResource(R.string.created, note.creationDate.format(root.context))
                }
                tvNoteTitle.text = note.title
                tvCreationDate.isVisible = isShowCreationDate
                tvNoteTitle.isVisible = note.title.isNotBlank()
                tvNoteTitle.maxLines = 3
                llLabels.isVisible = labels.isNotEmpty()
                if (note.title.isBlank() && previewSize == 0) {
                    tvNoteBody.text = note.body.takeLines(1)
                    tvNoteBody.maxLines = 1
                    tvNoteBody.isVisible = true
                } else {
                    tvNoteBody.text = note.body.takeLines(previewSize)
                    tvNoteBody.maxLines = previewSize
                    tvNoteBody.isVisible = previewSize != 0 && note.body.isNotBlank()
                }
                tvNoteTitle.setPadding(0.dp, 0.dp, 0.dp, if (note.body.isBlank() || previewSize == 0) 0.dp else 4.dp)
                tvNoteBody.setPadding(if (note.title.isBlank()) 0.dp else 4.dp, 0.dp, 0.dp, 0.dp)
                labels.forEach { label ->
                    NoteLabelItemBinding.inflate(layoutInflater, parent, false).withBinding {
                        tvLabel.text = label.title
                        tvLabel.background?.mutate()?.setTint(context.colorResource(color.toResource()))
                        tvLabel.setTextColor(context.colorAttributeResource(R.attr.notoBackgroundColor))
                        llLabels.addView(root)
                    }
                }
            }
        }
    }
}