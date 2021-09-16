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
import com.noto.app.util.*

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
    open var isShowCreationDate: Boolean = false

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    override fun bind(holder: Holder) = holder.bind()

    inner class Holder : EpoxyHolder() {
        private lateinit var binding: NoteItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteItemBinding.bind(itemView)
        }

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind() = with(binding) {
            tvNoteTitle.text = note.title
            if (isShowCreationDate) {
                val createdText = root.resources.stringResource(R.string.created)
                val formattedCreationDate = note.creationDate.format(root.context)
                tvCreationDate.text = "$createdText $formattedCreationDate"
            }
            tvCreationDate.isVisible = isShowCreationDate
            tvNoteTitle.isVisible = note.title.isNotBlank()
            root.setOnClickListener(onClickListener)
            root.setOnLongClickListener(onLongClickListener)
            tvNoteTitle.setBoldFont(font)
            tvNoteBody.setSemiboldFont(font)
            ibDrag.isVisible = isManualSorting
            ibDrag.setOnTouchListener(onDragHandleTouchListener)
            if (note.title.isBlank() && previewSize == 0) {
                tvNoteBody.text = note.body.takeLines(1)
                tvNoteBody.maxLines = 1
                tvNoteBody.isVisible = true
            } else {
                tvNoteBody.text = note.body.takeLines(previewSize)
                tvNoteBody.maxLines = previewSize
                tvNoteBody.isVisible = previewSize != 0 && note.body.isNotBlank()
            }
        }
    }
}