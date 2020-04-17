package com.noto

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noto.database.NotoColor
import com.noto.database.NotoIcon
import com.noto.databinding.DialogNotoBinding
import com.noto.databinding.ListItemNotoIconBinding
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import com.noto.util.getImageResource

class NotoDialog(context: Context, private val viewModel: ViewModel) : BottomSheetDialog(context, R.style.Style_BottomSheetDialog),
    OnNotoIconClickListener {

    private var notoColor = NotoColor.GRAY

    private var notoIcon = NotoIcon.LIST

    private val dialogBinding = DialogNotoBinding.inflate(layoutInflater)

    private val rvAdapter by lazy {
        NotoIconListRVAdapter(this)
    }

    private val rvLayoutManager by lazy {
        GridLayoutManager(context, 4)
    }

    init {
        create()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        show()
        dialogBinding.et.requestFocus()
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dialogBinding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DialogAnimation)

        dialogBinding.rbtnGray.setOnClickListener {
            notoColor = NotoColor.GRAY
            setNotoColor()
        }
        dialogBinding.rbtnBlue.setOnClickListener {
            notoColor = NotoColor.BLUE
            setNotoColor()
        }
        dialogBinding.rbtnPink.setOnClickListener {
            notoColor = NotoColor.PINK
            setNotoColor()
        }
        dialogBinding.rbtnCyan.setOnClickListener {
            notoColor = NotoColor.CYAN
            setNotoColor()
        }
        dialogBinding.cancelBtn.setOnClickListener {
            dismiss()
        }

        dialogBinding.rvNotoIcon.adapter = rvAdapter

        when (viewModel) {
            is NotebookListViewModel -> {
                dialogBinding.til.setStartIconDrawable(R.drawable.ic_notebook_24dp)
                rvAdapter.submitList(NotoIcon.values().toList().minus(NotoIcon.LIST))
            }
            is TodolistListViewModel -> {
                dialogBinding.til.setStartIconDrawable(R.drawable.ic_list_24dp)
                rvAdapter.submitList(NotoIcon.values().toList().minus(NotoIcon.NOTEBOOK))
            }
        }
        dialogBinding.createBtn.setOnClickListener {
            val title = dialogBinding.et.text.toString()

            if (title.isBlank()) {

                dialogBinding.til.error = "Title can't be empty"

            } else {

                when (viewModel) {
                    is NotebookListViewModel -> {
                        notebookListViewModel(title)
                    }
                    is TodolistListViewModel -> {
                        todolistListViewModel(title)
                    }
                    else -> {
                    }
                }
            }
        }

        dialogBinding.til.setStartIconOnClickListener {
            dialogBinding.listNotoIcon.visibility = View.VISIBLE
            dialogBinding.rvNotoIcon.layoutManager = rvLayoutManager
            this.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun notebookListViewModel(title: String) {
        viewModel as NotebookListViewModel

        if (viewModel.notebooks.value!!.any { it.notebookTitle == title }) {

            dialogBinding.til.error = "Title already exists"

        } else {

            val notebook =
                Notebook(notebookTitle = title, notoColor = notoColor, notebookPosition = viewModel.notebooks.value!!.size, notoIcon = notoIcon)

            viewModel.saveNotebook(notebook)

            dismiss()
        }
    }

    private fun todolistListViewModel(title: String) {

        viewModel as TodolistListViewModel

        if (viewModel.todolists.value!!.any { it.todolistTitle == title }) {

            dialogBinding.til.error = "Title already exists"

        } else {

            val todolist =
                Todolist(todolistTitle = title, notoColor = notoColor, todolistPosition = viewModel.todolists.value!!.size, notoIcon = notoIcon)

            viewModel.saveTodolist(todolist)

            dismiss()
        }
    }

    private fun setNotoColor() {
        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(notoColor.getColorPrimary(context))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(notoColor.getColorOnPrimary(context))
    }

    override fun onClick(notoIcon: NotoIcon) {
        this.notoIcon = notoIcon
        dialogBinding.til.setStartIconDrawable(notoIcon.getImageResource())
        dialogBinding.listNotoIcon.visibility = View.GONE
    }
}

private class NotoIconListRVAdapter(private val onNotoIconClickListener: OnNotoIconClickListener) :
    ListAdapter<NotoIcon, NotoIconListRVAdapter.NotoIconItemViewHolder>(NotoDialogIconDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotoIconItemViewHolder {
        return NotoIconItemViewHolder.create(parent, onNotoIconClickListener)
    }

    override fun onBindViewHolder(holder: NotoIconItemViewHolder, position: Int) {
        val notoIcon = getItem(position)
        holder.notoIcon = notoIcon
        holder.bind(notoIcon)
    }

    private class NotoIconItemViewHolder(private val binding: ListItemNotoIconBinding, private val onNotoIconClickListener: OnNotoIconClickListener) :
        RecyclerView.ViewHolder(binding.root) {


        lateinit var notoIcon: NotoIcon

        init {
            binding.root.setOnClickListener {
                onNotoIconClickListener.onClick(notoIcon)
            }
        }


        companion object {

            fun create(parent: ViewGroup, onNotoIconClickListener: OnNotoIconClickListener): NotoIconItemViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemNotoIconBinding.inflate(layoutInflater, parent, false)

                val colorStateList = binding.root.resources.getColorStateList(R.color.colorOnPrimary_600, null)
                val rippleDrawable = RippleDrawable(colorStateList, null, null)
                binding.root.background = rippleDrawable

                return NotoIconItemViewHolder(binding, onNotoIconClickListener)
            }
        }

        fun bind(notoIcon: NotoIcon) {
            binding.icon.setImageResource(notoIcon.getImageResource())
        }
    }

    private class NotoDialogIconDiffCallback() : DiffUtil.ItemCallback<NotoIcon>() {
        override fun areItemsTheSame(oldItem: NotoIcon, newItem: NotoIcon): Boolean {
            return oldItem.ordinal == newItem.ordinal
        }

        override fun areContentsTheSame(oldItem: NotoIcon, newItem: NotoIcon): Boolean {
            return oldItem == newItem
        }
    }
}

private interface OnNotoIconClickListener {
    fun onClick(notoIcon: NotoIcon)
}

class BottomSheetDialog() : BottomSheetDialogFragment() {
    private val binding by lazy { DialogNotoBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (view?.parent?.parent?.parent as View?)?.fitsSystemWindows = false

        return binding.root
    }
}

