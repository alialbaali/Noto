package com.noto.app.library

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.BaseItemTouchHelperListener
import com.noto.app.R
import com.noto.app.databinding.ItemLibraryBinding
import com.noto.app.util.getValue
import com.noto.domain.model.Library


class LibraryListRVAdapter(private val listener: LibraryItemClickListener) : ListAdapter<Library, LibraryListRVAdapter.LibraryItemViewHolder>(
    NotoItemDiffCallback()
), BaseItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemViewHolder {
        return LibraryItemViewHolder.create(parent, listener)
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


    @SuppressLint("ClickableViewAccessibility")
    class LibraryItemViewHolder(private val binding: ItemLibraryBinding, private val listener: LibraryItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        lateinit var library: Library

        private val resources = binding.root.resources

        init {

            binding.root.setOnClickListener {
                listener.onClick(library)
            }

            binding.root.setOnLongClickListener {
                listener.onLongClick(library)
                true
            }

//            binding.ivDrag.setOnTouchListener { v, event ->
//                v.performClick()
//                binding.root.animate().scaleX(0.90F).scaleY(0.90F).setDuration(50)
//                true
//            }

        }

        companion object {

            // Create ViewHolder Instance
            fun create(parent: ViewGroup, listener: LibraryItemClickListener): LibraryItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLibraryBinding.inflate(layoutInflater, parent, false)

                return LibraryItemViewHolder(binding, listener)
            }
        }

        fun bind(library: Library) {
            binding.library = library
            binding.executePendingBindings()

            val notoColor = ResourcesCompat.getColor(resources, library.notoColor.getValue(), null)
            val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(backgroundColor, backgroundColor, backgroundColor, backgroundColor, backgroundColor, backgroundColor, notoColor)
            ).apply {
                gradientType = GradientDrawable.LINEAR_GRADIENT
                cornerRadius = 16f
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                binding.root.outlineAmbientShadowColor = notoColor
                binding.root.outlineSpotShadowColor = notoColor
            }


            val rippleDrawable = RippleDrawable(ColorStateList.valueOf(notoColor), gradientDrawable, gradientDrawable)

            binding.root.background = rippleDrawable

            val count = listener.countLibraryNotos(library)

            binding.tvLibraryNotoCount.text = "$count".plus(if (count == 1) " Noto" else " Notos")
            binding.tvLibraryTitle.setTextColor(notoColor)
            binding.tvLibraryNotoCount.setTextColor(notoColor)
            binding.ivLibraryNotoIcon.setImageResource(library.notoIcon.getValue())
            binding.ivLibraryNotoIcon.imageTintList = ColorStateList.valueOf(notoColor)

            setDefaultBackground()
        }

        fun drag(library: Library) {
            binding.root.animate().scaleX(0.90F).scaleY(0.90F).setDuration(50)
        }

        fun clear(library: Library) {
            binding.root.animate().scaleX(1F).scaleY(1F).setDuration(50)
        }

        private fun setDefaultBackground() {
//            binding.root.background = rippleDrawable
//            binding.root.backgroundTintList = ColorStateList.valueOf(itemView.resources.getColor(library.notoColor.getValue(), null))
        }
    }

    private class NotoItemDiffCallback() : DiffUtil.ItemCallback<Library>() {

        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem.libraryId == newItem.libraryId

        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem == newItem

    }
}

interface LibraryItemClickListener {

    fun onClick(library: Library)

    fun onLongClick(library: Library)

    fun countLibraryNotos(library: Library): Int

}