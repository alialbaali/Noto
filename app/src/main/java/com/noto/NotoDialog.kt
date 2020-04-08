package com.noto

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import com.noto.database.NotoColor
import com.noto.databinding.DialogNotoBinding
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary

class NotoDialog(context: Context, private val viewModel: ViewModel) : AlertDialog(context) {

    private var notoColor = NotoColor.GRAY

    private val dialogBinding = DialogNotoBinding.inflate(layoutInflater)

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
    }

    private fun notebookListViewModel(title: String) {
        viewModel as NotebookListViewModel

        if (viewModel.notebooks.value!!.any { it.notebookTitle == title }) {

            dialogBinding.til.error = "Title already exists"

        } else {

            val notebook = Notebook(notebookTitle = title, notoColor = notoColor)

            viewModel.saveNotebook(notebook)

            dismiss()
        }
    }

    private fun todolistListViewModel(title: String) {

        viewModel as TodolistListViewModel

        if (viewModel.todolists.value!!.any { it.todolistTitle == title }) {

            dialogBinding.til.error = "Title already exists"

        } else {

            val todolist = Todolist(todolistTitle = title, notoColor = notoColor)

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
}