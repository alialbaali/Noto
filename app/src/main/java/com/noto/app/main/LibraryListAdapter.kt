package com.noto.app.main

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.databinding.LibraryItemBinding
import com.noto.app.domain.model.Library
import com.noto.app.main.LibraryListAdapter.LibraryItemViewHolder
import com.noto.app.util.colorResource
import com.noto.app.util.stringResource
import com.noto.app.util.toCountText
import com.noto.app.util.toResource
import java.io.Serializable


class LibraryListAdapter(private val listener: LibraryItemClickListener) : ListAdapter<Library, LibraryItemViewHolder>(LibraryItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryItemViewHolder = LibraryItemViewHolder.create(parent, listener)

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
            fun create(parent: ViewGroup, listener: LibraryItemClickListener): LibraryItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LibraryItemBinding.inflate(layoutInflater, parent, false)
                return LibraryItemViewHolder(binding, listener)
            }
        }

        fun bind(library: Library) {
            binding.tvLibraryTitle.text = library.title
            val resources = binding.root.resources
            library.setupColors()
            val count = listener.countLibraryNotes(library)
            binding.tvLibraryNotesCount.text = count.toCountText(resources.stringResource(R.string.note), resources.stringResource(R.string.notes))
        }

        private fun Library.setupColors() {
            val notoColor = binding.root.resources.colorResource(color.toResource())
            binding.vColor.background.setTint(notoColor)
            binding.tvLibraryTitle.setTextColor(notoColor)
            binding.tvLibraryNotesCount.setTextColor(notoColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                binding.root.outlineAmbientShadowColor = notoColor
                binding.root.outlineSpotShadowColor = notoColor
            }
        }
    }

    private class LibraryItemDiffCallback : DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean = oldItem == newItem
    }

    interface LibraryItemClickListener : Serializable {
        fun onClick(library: Library)
        fun onLongClick(library: Library)
        fun countLibraryNotes(library: Library): Int
    }
}

