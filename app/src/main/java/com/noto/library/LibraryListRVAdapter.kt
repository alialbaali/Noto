package com.noto.library

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.BaseItemTouchHelperListener
import com.noto.R
import com.noto.databinding.ItemLibraryBinding
import com.noto.domain.model.Library
import com.noto.util.getValue

class LibraryListRVAdapter(private val viewModel: LibraryListViewModel, private val listener: LibraryItemClickListener?) :
    ListAdapter<Library, LibraryListRVAdapter.LibraryItemViewHolder>(NotoItemDiffCallback()),
    BaseItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemViewHolder {
        return LibraryItemViewHolder.create(parent, viewModel, listener)
    }

    override fun onBindViewHolder(holderList: LibraryItemViewHolder, position: Int) {
        val notoList = getItem(position)
        holderList.library = notoList
        holderList.bind(notoList)
    }

    override fun onMoveViewHolder(fromViewHolder: RecyclerView.ViewHolder, toViewHolder: RecyclerView.ViewHolder) {
        fromViewHolder as LibraryItemViewHolder
        toViewHolder as LibraryItemViewHolder

        val fromLibrary = currentList.find { it.libraryPosition == fromViewHolder.adapterPosition }!!

        val toLibrary = currentList.find { it.libraryPosition == toViewHolder.adapterPosition }!!

        fromLibrary.libraryPosition = toLibrary.libraryPosition.also { toLibrary.libraryPosition = fromLibrary.libraryPosition }

        fromViewHolder.drag(fromViewHolder.library)

        notifyItemMoved(fromViewHolder.adapterPosition, toViewHolder.adapterPosition)
    }

    override fun onClearViewHolder(viewHolder: RecyclerView.ViewHolder) {
        viewHolder as LibraryItemViewHolder
        viewHolder.clear(viewHolder.library)
//        viewModel.updateSortMethod(SortMethod.Custom)
//        viewModel.updateNotebooks(currentList)
    }


    // Notebook Item ViewHolder
    class LibraryItemViewHolder(
        private val binding: ItemLibraryBinding,
        private val libraryListViewModel: LibraryListViewModel,
        private val listener: LibraryItemClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var library: Library

        private val resources = binding.root.resources

        private val drawable = resources.getDrawable(R.drawable.shape_item_library, null)

        private val rippleDrawable by lazy {
            RippleDrawable(ColorStateList.valueOf(resources.getColor(R.color.colorPrimary)), drawable, drawable)
        }

        init {
            binding.root.let { it.setOnClickListener { listener?.onClick(library) } }
        }

        companion object {

            // Create ViewHolder Instance
            fun create(parent: ViewGroup, libraryListViewModel: LibraryListViewModel, libraryItemClickListener: LibraryItemClickListener?)
                    : LibraryItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLibraryBinding.inflate(layoutInflater, parent, false)

                return LibraryItemViewHolder(
                    binding,
                    libraryListViewModel,
                    libraryItemClickListener
                )
            }
        }

        // Bind notebook's values to the list item
        fun bind(library: Library) {
            binding.library = library
            binding.ivLibraryNotoIcon.setImageResource(library.notoIcon.getValue())
            val notosCount = libraryListViewModel.countNotos(library.libraryId)
            if (notosCount == 1) {
                binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Noto")
            } else {
                binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Notos")
            }
            binding.executePendingBindings()

            setDefaultBackground()
        }

        fun drag(library: Library) {
            binding.root.elevation = resources.getDimension(R.dimen.elevation_normal)
        }

        fun clear(library: Library) {
            binding.root.elevation = resources.getDimension(R.dimen.elevation_extra_small)
            setDefaultBackground()
        }

        private fun setDefaultBackground() {
            binding.root.background = rippleDrawable
            binding.root.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(library.notoColor.getValue(), null))
        }
    }

    // Notebook Item Difference Callback
    private class NotoItemDiffCallback() : DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem.libraryId == newItem.libraryId
        }

        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem == newItem
        }
    }
}

interface LibraryItemClickListener {
    fun onClick(library: Library)
}