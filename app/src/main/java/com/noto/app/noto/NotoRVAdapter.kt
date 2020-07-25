package com.noto.app.noto

import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.databinding.ItemBlockBinding
import timber.log.Timber

class NotoRVAdapter(private val blockListener: BlockListener) : ListAdapter<String, NotoRVAdapter.StringViewHolder>(
    StringItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
        return StringViewHolder.createViewHolder(parent, blockListener)
    }

    override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
        val block = getItem(position)
        holder.bind(block)
        holder.block = block

    }


    class StringViewHolder(val binding: ItemBlockBinding, private val blockListener: BlockListener) : RecyclerView.ViewHolder(binding.root) {

        lateinit var block: String

        init {
            binding.editTextTextPersonName.setOnKeyListener { v, keyCode, event ->
                if (event.keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    blockListener.onBack(adapterPosition)
                    Timber.i("BACK_KEY")
                    return@setOnKeyListener true
                } else if (event.action == KeyEvent.ACTION_DOWN && event.action == KeyEvent.KEYCODE_ENTER) {
                    blockListener.onClick(adapterPosition)
                    Timber.i("KEY")
                    return@setOnKeyListener true
                }
                Timber.i(keyCode.toString())
                Timber.i(event.action.toString())
                Timber.i(event.keyCode.toString())
                Timber.i(event.toString())
                return@setOnKeyListener false
            }

            binding.editTextTextPersonName.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    blockListener.onClick(adapterPosition)
                    Timber.i("ACTION")
                    return@setOnEditorActionListener true
                }
                Timber.i(actionId.toString())
                Timber.i(event.action.toString())
                Timber.i(event.keyCode.toString())
                Timber.i(event.toString())
                return@setOnEditorActionListener false
            }
        }


        companion object {
            fun createViewHolder(parent: ViewGroup, blockListener: BlockListener): StringViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBlockBinding.inflate(layoutInflater, parent, false)

                return StringViewHolder(binding, blockListener)
            }
        }

        fun bind(block: String) {
            binding.editTextTextPersonName.setText(block)
            binding.editTextTextPersonName.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
        }
    }


    class StringItemDiffCallback() : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

}

interface BlockListener {

    fun onClick(adapterPosition: Int)

    fun onBack(adapterPosition: Int)
}