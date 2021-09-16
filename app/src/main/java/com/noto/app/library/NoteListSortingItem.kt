package com.noto.app.library

import android.annotation.SuppressLint
import android.view.View
import androidx.core.graphics.ColorUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoteListSortingItemBinding
import com.noto.app.domain.model.NoteListSorting
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_list_sorting_item)
abstract class NoteListSortingItem : EpoxyModelWithHolder<NoteListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sorting: NoteListSorting

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    var notesCount: Int = 0

    @EpoxyAttribute
    lateinit var notoColor: NotoColor

    override fun bind(holder: Holder) = with(holder.binding) {
        val resources = root.resources
        val sortingText = resources.stringResource(R.string.sorting).lowercase()
        val color = resources.colorResource(notoColor.toResource())
        tvSorting.text = when (sorting) {
            NoteListSorting.Manual -> "${resources.stringResource(R.string.manual)} $sortingText"
            NoteListSorting.CreationDate -> "${resources.stringResource(R.string.creation_date)} $sortingText"
            NoteListSorting.Alphabetical -> "${resources.stringResource(R.string.alphabetical)} $sortingText"
        }
        tvSorting.setOnClickListener(onClickListener)
        root.rootView.setFullSpan()
        val arrowDrawable = when (sortingOrder) {
            SortingOrder.Ascending -> R.drawable.ic_round_arrow_up_24
            SortingOrder.Descending -> R.drawable.ic_round_arrow_down_24
        }
        if (sorting == NoteListSorting.Manual)
            tvSorting.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        else
            tvSorting.setCompoundDrawablesWithIntrinsicBounds(arrowDrawable, 0, 0, 0)
        tvLibraryNotesCount.text = notesCount.toCountText(
            resources.stringResource(R.string.note),
            resources.stringResource(R.string.notes)
        )
        tvSorting.background?.mutate()?.setTint(ColorUtils.setAlphaComponent(color, 25))
        tvSorting.compoundDrawables[0]?.mutate()?.setTint(color)
        tvLibraryNotesCount.setTextColor(color)
        tvSorting.setTextColor(color)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NoteListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteListSortingItemBinding.bind(itemView)
        }
    }

}