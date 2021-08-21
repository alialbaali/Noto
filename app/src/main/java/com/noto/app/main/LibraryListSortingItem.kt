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
    lateinit var onClickListener: View.OnClickListener

    @SuppressLint("SetTextI18n")
    override fun bind(holder: Holder) {
        val resources = holder.binding.root.resources
        val sortingText = resources.stringResource(R.string.sorting).lowercase()
        holder.binding.tvSorting.text = when (sorting) {
            LibraryListSorting.Manual -> "${resources.stringResource(R.string.manual)} $sortingText"
            LibraryListSorting.CreationDate -> "${resources.stringResource(R.string.creation_date)} $sortingText"
            LibraryListSorting.Alphabetical -> "${resources.stringResource(R.string.alphabetical)} $sortingText"
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

