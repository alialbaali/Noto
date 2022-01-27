package com.noto.app.main

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.FolderListSortingItemBinding
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.pluralsResource
import com.noto.app.util.setFullSpan
import com.noto.app.util.stringResource

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.folder_list_sorting_item)
abstract class FolderListSortingItem : EpoxyModelWithHolder<FolderListSortingItem.Holder>() {

    @EpoxyAttribute
    lateinit var sortingType: FolderListSortingType

    @EpoxyAttribute
    lateinit var sortingOrder: SortingOrder

    @EpoxyAttribute
    var librariesCount: Int = 0

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        root.context?.let { context ->
            tvSorting.text = when (sortingType) {
                FolderListSortingType.Manual -> context.stringResource(R.string.manual_sorting)
                FolderListSortingType.CreationDate -> context.stringResource(R.string.creation_date_sorting)
                FolderListSortingType.Alphabetical -> context.stringResource(R.string.alphabetical_sorting)
            }
            tvLibrariesCount.text = context.pluralsResource(R.plurals.folders_count, librariesCount, librariesCount).lowercase()
        }
        tvSorting.setOnClickListener(onClickListener)
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: FolderListSortingItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = FolderListSortingItemBinding.bind(itemView)
        }
    }
}

