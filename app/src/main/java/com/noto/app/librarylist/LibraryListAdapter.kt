package com.noto.app.librarylist

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.databinding.LibraryItemBinding
import com.noto.app.domain.model.Library
import com.noto.app.librarylist.LibraryListAdapter.LibraryItemViewHolder
import com.noto.app.util.colorResource
import com.noto.app.util.toResource


class LibraryListAdapter(private val listener: LibraryItemClickListener) : ListAdapter<Library, LibraryItemViewHolder>(LibraryItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemViewHolder {
        return LibraryItemViewHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holderList: LibraryItemViewHolder, position: Int) {
        val library = getItem(position)
        holderList.library = library
        holderList.bind(library)
    }

    class LibraryItemViewHolder(
        private val binding: LibraryItemBinding,
        private val listener: LibraryItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var library: Library

        init {
            binding.root.setOnClickListener {
                listener.onClick(library)
            }
            binding.root.setOnLongClickListener {
                listener.onLongClick(library)
                true
            }
        }

        companion object {

            fun create(
                parent: ViewGroup,
                listener: LibraryItemClickListener
            ): LibraryItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LibraryItemBinding.inflate(layoutInflater, parent, false)

                return LibraryItemViewHolder(binding, listener)
            }
        }

        fun bind(library: Library) {
            binding.tvLibraryTitle.text = library.title

            val notoColor = binding.root.colorResource(library.color.toResource())
            val backgroundColor = binding.root.colorResource(R.color.colorBackground)

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

            val count = listener.countLibraryNotes(library)

            binding.tvLibraryNotoCount.text = "$count".plus(if (count == 1) " Note" else " Notes")
            binding.tvLibraryTitle.setTextColor(notoColor)
            binding.tvLibraryNotoCount.setTextColor(notoColor)
            binding.ivLibraryNotoIcon.setImageResource(library.icon.toResource())
            binding.ivLibraryNotoIcon.imageTintList = ColorStateList.valueOf(notoColor)
        }
    }

    private class LibraryItemDiffCallback : DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem == newItem
    }

    interface LibraryItemClickListener {
        fun onClick(library: Library)
        fun onLongClick(library: Library)
        fun countLibraryNotes(library: Library): Int
    }
}

