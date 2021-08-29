package com.noto.app.library

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.util.setBoldFont
import com.noto.app.util.setSemiboldFont
import com.noto.app.util.takeLines

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_item)
abstract class NoteItem : EpoxyModelWithHolder<NoteItem.Holder>() {
    @EpoxyAttribute
    lateinit var note: Note

    @EpoxyAttribute
    lateinit var font: Font

    @EpoxyAttribute
    var previewSize: Int = 0

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onLongClickListener: View.OnLongClickListener

    override fun bind(holder: Holder) {
        holder.binding.tvNoteTitle.text = note.title
        holder.binding.rbNoteStar.isVisible = note.isStarred
        holder.binding.tvNoteTitle.isVisible = note.title.isNotBlank()
        holder.binding.root.setOnClickListener(onClickListener)
        holder.binding.root.setOnLongClickListener(onLongClickListener)
        holder.binding.tvNoteTitle.setBoldFont(font)
        holder.binding.tvNoteBody.setSemiboldFont(font)
        if (note.title.isBlank() && previewSize == 0) {
            holder.binding.tvNoteBody.text = note.body.takeLines(1)
            holder.binding.tvNoteBody.maxLines = 1
            holder.binding.tvNoteBody.isVisible = true
        } else {
            holder.binding.tvNoteBody.text = note.body.takeLines(previewSize)
            holder.binding.tvNoteBody.maxLines = previewSize
            holder.binding.tvNoteBody.isVisible = previewSize != 0 && note.body.isNotBlank()
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NoteItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteItemBinding.bind(itemView)
        }
    }
}