package com.noto.app.library

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
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.noto.app.R
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.label.noteLabelItem
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_item)
abstract class NoteItem : EpoxyModelWithHolder<NoteItem.Holder>() {
    @EpoxyAttribute
    lateinit var note: Note

    @EpoxyAttribute
    lateinit var font: Font

    @EpoxyAttribute
    lateinit var labels: List<Label>

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute
    var previewSize: Int = 0

    @EpoxyAttribute
    open var isShowCreationDate: Boolean = false

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onLongClickListener: View.OnLongClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onDragHandleTouchListener: View.OnTouchListener

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        tvNoteTitle.text = note.title
        if (isShowCreationDate)
            tvCreationDate.text = root.resources.stringResource(R.string.created, note.creationDate.format(root.context))
        tvCreationDate.isVisible = isShowCreationDate
        tvNoteTitle.isVisible = note.title.isNotBlank()
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        tvNoteTitle.setBoldFont(font)
        tvNoteBody.setSemiboldFont(font)
        ibDrag.isVisible = isManualSorting
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
        rv.isVisible = labels.isNotEmpty()
        rv.layoutManager = FlexboxLayoutManager(root.context, FlexDirection.ROW, FlexWrap.WRAP)
        if (rv.itemDecorationCount == 0) {
            val itemDecoration = FlexboxItemDecoration(root.context).apply {
                setDrawable(root.resources.drawableResource(R.drawable.note_label_item_decoration))
                setOrientation(FlexboxItemDecoration.HORIZONTAL)
            }
            rv.addItemDecoration(itemDecoration)
        }
        rv.withModels {
            labels.forEach { label ->
                noteLabelItem {
                    id(label.id)
                    label(label)
                    color(color)
                }
            }
        }
        if (note.title.isBlank() && previewSize == 0) {
            tvNoteBody.text = note.body.takeLines(1)
            tvNoteBody.maxLines = 1
            tvNoteBody.isVisible = true
        } else {
            tvNoteBody.text = note.body.takeLines(previewSize)
            tvNoteBody.maxLines = previewSize
            tvNoteBody.isVisible = previewSize != 0 && note.body.isNotBlank()
        }
        tvNoteTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(bottom = if (note.body.isBlank() || previewSize == 0) 0.dp else 4.dp)
        }
        tvNoteBody.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(top = if (note.title.isBlank()) 0.dp else 4.dp)
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