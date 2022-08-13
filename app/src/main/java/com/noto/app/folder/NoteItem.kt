package com.noto.app.folder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.noto.app.R
import com.noto.app.databinding.NoteItemBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.NotoColor
import com.noto.app.label.noteLabelItem
import com.noto.app.util.*

private const val WidthRatio = 0.9F

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_item)
abstract class NoteItem : EpoxyModelWithHolder<NoteItem.Holder>() {
    @EpoxyAttribute
    lateinit var model: NoteItemModel

    @EpoxyAttribute
    lateinit var font: Font

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute
    lateinit var searchTerm: String

    @EpoxyAttribute
    var previewSize: Int = 0

    @EpoxyAttribute
    open var isShowCreationDate: Boolean = false

    @EpoxyAttribute
    open var isShowAccessDate: Boolean = false

    @EpoxyAttribute
    open var isManualSorting: Boolean = false

    @EpoxyAttribute
    open var isSelection: Boolean = false

    @EpoxyAttribute
    open var isPreview: Boolean = false

    @EpoxyAttribute
    var parentWidth: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickListener: View.OnClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onLongClickListener: View.OnLongClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onDragHandleTouchListener: View.OnTouchListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onSelectListener: View.OnClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onDeselectListener: View.OnClickListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val colorResource = context.colorResource(color.toResource())
            ll.background?.setRippleColor(colorResource.toColorStateList())
            tvNoteTitle.setLinkTextColor(colorResource)
            tvNoteBody.setLinkTextColor(colorResource)
            tvNoteTitle.text = model.note.title.highlightText(colorResource, context)
            if (model.note.title.isBlank() && previewSize == 0) {
                tvNoteBody.text = model.note.body.takeLines(1).highlightText(colorResource, context)
                tvNoteBody.maxLines = 1
                tvNoteBody.isVisible = true
            } else {
                tvNoteBody.text = model.note.body.takeLines(previewSize).highlightText(colorResource, context)
                tvNoteBody.isVisible = previewSize != 0 && model.note.body.isNotBlank()
                if (isPreview) {
                    tvNoteTitle.maxLines = 3
                    tvNoteBody.maxLines = 5
                } else {
                    tvNoteTitle.maxLines = Int.MAX_VALUE
                    tvNoteBody.maxLines = previewSize
                }
            }
            if (isShowCreationDate)
                tvCreationDate.text = context.stringResource(R.string.created, model.note.creationDate.format(root.context))
            if (isShowAccessDate)
                tvAccessDate.text = context.stringResource(R.string.accessed, model.note.accessDate.format(root.context))
            if (model.note.reminderDate != null) {
                llReminder.background?.mutate()?.setTint(colorResource)
                tvReminder.text = model.note.reminderDate?.format(context)
            }
            ivSelected.imageTintList = colorResource.toColorStateList()
        }
        tvCreationDate.isVisible = isShowCreationDate
        tvAccessDate.isVisible = isShowAccessDate
        tvNoteTitle.isVisible = model.note.title.isNotBlank()
        llReminder.isVisible = model.note.reminderDate != null
        ivSelected.isVisible = model.isSelected
        ivSelected.isSelected = model.isSelected
        if (isSelection) {
            if (model.isSelected) {
                root.setOnClickListener(onDeselectListener)
                root.setOnLongClickListener {
                    onDeselectListener?.onClick(it)
                    true
                }
            } else {
                root.setOnClickListener(onSelectListener)
                root.setOnLongClickListener {
                    onSelectListener?.onClick(it)
                    true
                }
            }
        } else {
            root.setOnClickListener(onClickListener)
            root.setOnLongClickListener(onLongClickListener)
        }
        tvNoteTitle.setSemiboldFont(font)
        tvNoteBody.setMediumFont(font)
        ibDrag.isVisible = isManualSorting
        ibDrag.setOnTouchListener(onDragHandleTouchListener)
        ll.isSelected = model.isSelected
        rv.isVisible = model.labels.isNotEmpty()
        rv.layoutManager = FlexboxLayoutManager(root.context, FlexDirection.ROW, FlexWrap.WRAP)
        rv.withModels {
            model.labels.forEach { label ->
                noteLabelItem {
                    id(label.id)
                    label(label)
                    color(color)
                }
            }
        }
        tvNoteTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(bottom = if (model.note.body.isBlank() || previewSize == 0) 0.dp else 4.dp)
        }
        tvNoteBody.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(top = if (model.note.title.isBlank()) 0.dp else 4.dp)
        }

        if (isCurrentLocaleArabic()) {
            tvCreationDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold)
            tvAccessDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold)
        } else {
            tvCreationDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
            tvAccessDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
        }

        val gestureDetectorListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                onLongClickListener?.onLongClick(root)
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (isSelection) {
                    if (model.isSelected) {
                        onDeselectListener?.onClick(root)
                    } else {
                        onSelectListener?.onClick(root)
                    }
                } else {
                    onClickListener?.onClick(root)
                }
                return true
            }
        }
        val gestureDetector = GestureDetector(root.context, gestureDetectorListener)
        rv.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_DOWN)
                ll.background?.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
            else if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL)
                ll.background?.state = intArrayOf(-android.R.attr.state_pressed, -android.R.attr.state_enabled)
            false
        }
        if (isPreview) {
            root.layoutParams.width = (parentWidth * WidthRatio).toInt()
            root.isEnabled = false
        } else {
            root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            root.isEnabled = true
        }
    }

    private fun String.highlightText(color: Int, context: Context): Spannable {
        return this.toSpannable().apply {
            val startIndex = this.indexOf(searchTerm, ignoreCase = true)
            val endIndex = startIndex + searchTerm.length
            val colorSpan = ForegroundColorSpan(color)
            val boldFontSpan = context.tryLoadingFontResource(R.font.nunito_black)?.style?.let(::StyleSpan)
            val boldSpan = StyleSpan(Typeface.BOLD)
            if (startIndex != -1) {
                setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(boldSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (boldFontSpan != null) {
                    setSpan(boldFontSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
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