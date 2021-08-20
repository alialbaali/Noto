package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LibraryListSortingItemBinding
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.stringResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.library_list_sorting_item)
abstract class LibraryListSortingItem : EpoxyModelWithHolder<LibraryListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sorting: LibraryListSorting

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) {
        val resources = holder.binding.root.resources
        val ascendingText = resources.stringResource(R.string.ascending)
        val descendingText = resources.stringResource(R.string.descending)
        holder.binding.tvSorting.text = when (sorting) {
            LibraryListSorting.Manually -> resources.stringResource(R.string.manually)
            LibraryListSorting.CreationDate -> {
                val text = resources.stringResource(R.string.creation_date)
                when (sortingOrder) {
                    SortingOrder.Ascending -> "$text - $ascendingText"
                    SortingOrder.Descending -> "$text - $descendingText"
                }
            }
            LibraryListSorting.Alphabetically -> {
                val text = resources.stringResource(R.string.alphabetically)
                when (sortingOrder) {
                    SortingOrder.Ascending -> "$text - $ascendingText"
                    SortingOrder.Descending -> "$text - $descendingText"
                }
            }
        }
        holder.binding.tvSorting.setOnClickListener(onClickListener)
        holder.binding.root.rootView.layoutParams.also {
            if (it != null && it is StaggeredGridLayoutManager.LayoutParams)
                it.isFullSpan = true
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LibraryListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LibraryListSortingItemBinding.bind(itemView)
        }
    }
}

