package com.noto.app.library

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.NoteListSortingItemBinding
import com.noto.app.domain.model.NoteListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.setFullSpan
import com.noto.app.util.stringResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.note_list_sorting_item)
abstract class NoteListSortingItem : EpoxyModelWithHolder<NoteListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sorting: NoteListSorting

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) {
        val resources = holder.binding.root.resources
        val sortingText = resources.stringResource(R.string.sorting).lowercase()
        holder.binding.tvSorting.text = when (sorting) {
            NoteListSorting.Manual -> "${resources.stringResource(R.string.manual)} $sortingText"
            NoteListSorting.CreationDate -> "${resources.stringResource(R.string.creation_date)} $sortingText"
            NoteListSorting.Alphabetical -> "${resources.stringResource(R.string.alphabetical)} $sortingText"
        }
        holder.binding.tvSorting.setOnClickListener(onClickListener)
        holder.binding.root.rootView.setFullSpan()
        val arrowDrawable = when (sortingOrder) {
            SortingOrder.Ascending -> R.drawable.ic_round_arrow_up_24
            SortingOrder.Descending -> R.drawable.ic_round_arrow_down_24
        }
        if (sorting == NoteListSorting.Manual)
            holder.binding.tvSorting.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        else
            holder.binding.tvSorting.setCompoundDrawablesWithIntrinsicBounds(arrowDrawable, 0, 0, 0)
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: NoteListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = NoteListSortingItemBinding.bind(itemView)
        }
    }

}