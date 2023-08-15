package com.noto.app.note

import android.annotation.SuppressLint
import android.view.View
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.UndoRedoItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*


private const val ParagraphLength = 50
private const val Delimiter = "..."
private const val WhiteSpace = ' '

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class UndoRedoItem : EpoxyModelWithHolder<UndoRedoItem.Holder>() {

    @EpoxyAttribute
    lateinit var text: String

    @EpoxyAttribute
    var index: Int = 0

    @EpoxyAttribute
    var cursorStartPosition: Int = 0

    @EpoxyAttribute
    var cursorEndPosition: Int = 0

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onCopyClickListener: View.OnClickListener

    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            val selectedColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
            val backgroundColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
            val colorPrimary = context.colorResource(color.toColorResourceId())
            val colorSecondary = context.colorAttributeResource(R.attr.notoSecondaryColor)
            ll.background?.mutate()?.setTint(if (isSelected) selectedColor else backgroundColor)
            tvIndex.text = index.plus(1).toString()
            tvText.text = buildSpannedString {
                if (text.isNotBlank()) {
                    val startIndex = 0
                    val endIndex = text.lastIndex
                    val diffStartIndex = cursorStartPosition.coerceIn(startIndex, endIndex)
                    val diffEndIndex = cursorEndPosition.coerceIn(startIndex, endIndex)
                    val startText = text.substring(startIndex until diffStartIndex)
                    val diffText = text.substring(diffStartIndex until diffEndIndex)
                    val endText = text.substring(diffEndIndex..endIndex)

                    color(colorSecondary) {
                        if (startText.length > ParagraphLength) {
                            append(Delimiter)
                            append(WhiteSpace)
                            append(startText.takeLast(ParagraphLength))
                        } else {
                            append(startText)
                        }
                    }

                    color(colorPrimary) {
                        bold {
                            append(diffText)
                        }
                    }

                    color(colorSecondary) {
                        if (endText.length > ParagraphLength) {
                            append(endText.take(ParagraphLength))
                            append(WhiteSpace)
                            append(Delimiter)
                        } else {
                            append(endText)
                        }
                    }
                }
            }
        }
        ll.setOnClickListener(onClickListener)
        ibCopy.setOnClickListener(onCopyClickListener)
    }

    override fun getDefaultLayout(): Int = R.layout.undo_redo_item

    class Holder : EpoxyHolder() {
        lateinit var binding: UndoRedoItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = UndoRedoItemBinding.bind(itemView)
        }
    }
}
