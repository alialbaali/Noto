package com.noto.app.library

import android.annotation.SuppressLint
import android.graphics.Typeface
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
import com.noto.app.util.fontResource
import com.noto.app.util.takeLines

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_item)
abstract class NoteItem : EpoxyModelWithHolder<NoteItem.Holder>() {
    @EpoxyAttribute
    lateinit var note: Note

    @EpoxyAttribute
    lateinit var font: Font

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var onLongClickListener: View.OnLongClickListener

    override fun bind(holder: Holder) {
        holder.binding.tvNoteTitle.text = note.title.takeLines(3)
        holder.binding.tvNoteBody.text = note.body.takeLines(5)
        holder.binding.rbNoteStar.isVisible = note.isStarred
        holder.binding.tvNoteTitle.isVisible = note.title.isNotBlank()
        holder.binding.tvNoteBody.isVisible = note.body.isNotBlank()
        holder.binding.root.setOnClickListener(onClickListener)
        holder.binding.root.setOnLongClickListener(onLongClickListener)

        val context = holder.binding.root.context
        val resources = holder.binding.root.resources

        when (font) {
            Font.Nunito -> {
                holder.binding.tvNoteTitle.typeface = resources.fontResource(context, R.font.nunito_bold)
                holder.binding.tvNoteBody.typeface = resources.fontResource(context, R.font.nunito_semibold)
            }
            Font.Monospace -> {
                holder.binding.tvNoteTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                holder.binding.tvNoteBody.typeface = Typeface.MONOSPACE
            }
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