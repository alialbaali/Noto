package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LibraryListSortingItemBinding
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.setFullSpan
import com.noto.app.util.stringResource
import com.noto.app.util.toCountText

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.library_list_sorting_item)
abstract class LibraryListSortingItem : EpoxyModelWithHolder<LibraryListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sorting: LibraryListSorting

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute
    var librariesCount: Int = 0

    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) = with(holder.binding) {
        val resources = root.resources
        val sortingText = resources.stringResource(R.string.sorting).lowercase()
        tvSorting.text = when (sorting) {
            LibraryListSorting.Manual -> "${resources.stringResource(R.string.manual)} $sortingText"
            LibraryListSorting.CreationDate -> "${resources.stringResource(R.string.creation_date)} $sortingText"
            LibraryListSorting.Alphabetical -> "${resources.stringResource(R.string.alphabetical)} $sortingText"
        }
        tvSorting.setOnClickListener(onClickListener)
        root.rootView.setFullSpan()
        val arrowDrawable = when (sortingOrder) {
            SortingOrder.Ascending -> R.drawable.ic_round_arrow_up_24
            SortingOrder.Descending -> R.drawable.ic_round_arrow_down_24
        }
        if (sorting == LibraryListSorting.Manual)
            tvSorting.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        else
            tvSorting.setCompoundDrawablesWithIntrinsicBounds(arrowDrawable, 0, 0, 0)
        tvLibrariesCount.text = librariesCount.toCountText(
            resources.stringResource(R.string.library),
            resources.stringResource(R.string.libraries)
        )
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LibraryListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LibraryListSortingItemBinding.bind(itemView)
        }
    }
}

