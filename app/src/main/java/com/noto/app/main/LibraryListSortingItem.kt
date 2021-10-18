package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.LibraryListSortingItemBinding
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.pluralsResource
import com.noto.app.util.setFullSpan
import com.noto.app.util.stringResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.library_list_sorting_item)
abstract class LibraryListSortingItem : EpoxyModelWithHolder<LibraryListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sortingType: LibraryListSortingType

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    var librariesCount: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        val resources = root.resources
        tvSorting.text = when (sortingType) {
            LibraryListSortingType.Manual -> resources.stringResource(R.string.manual_sorting)
            LibraryListSortingType.CreationDate -> resources.stringResource(R.string.creation_date_sorting)
            LibraryListSortingType.Alphabetical -> resources.stringResource(R.string.alphabetical_sorting)
        }
        tvSorting.setOnClickListener(onClickListener)
        tvLibrariesCount.text = resources.pluralsResource(R.plurals.libraries_count, librariesCount, librariesCount).lowercase()
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: LibraryListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = LibraryListSortingItemBinding.bind(itemView)
        }
    }
}

