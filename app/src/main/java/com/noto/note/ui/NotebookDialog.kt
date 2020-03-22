package com.noto.note.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.noto.R
import com.noto.databinding.DialogNotebookBinding
import com.noto.note.model.Notebook
import com.noto.note.model.NotebookColor

class NotebookDialog(context: Context) : AlertDialog(context) {
    private val resources = context.resources

    internal val dialogBinding = DialogNotebookBinding.inflate(LayoutInflater.from(context))

    lateinit var notebook: Notebook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dialogBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DialogAnimation)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialogBinding.et.requestFocus()

        dialogBinding.notebook = notebook

        when (notebook.notebookColor) {
            NotebookColor.GRAY -> setGray()
            NotebookColor.BLUE -> setBlue()
            NotebookColor.PINK -> setPink()
            NotebookColor.CYAN -> setCyan()
        }

        dialogBinding.rbtnGray.setOnClickListener {
            setGray()
        }
        dialogBinding.rbtnBlue.setOnClickListener {
            setBlue()
        }
        dialogBinding.rbtnPink.setOnClickListener {
            setPink()
        }
        dialogBinding.rbtnCyan.setOnClickListener {
            setCyan()
        }
        dialogBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setGray() {

        notebook.notebookColor = NotebookColor.GRAY

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.gray_primary, null)

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_gray_drawable, null)

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.gray_primary_dark, null))
    }

    private fun setBlue() {

        notebook.notebookColor = NotebookColor.BLUE

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_blue_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.blue_primary, null)

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.blue_primary_dark, null))
    }

    private fun setPink() {

        notebook.notebookColor = NotebookColor.PINK

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_pink_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.pink_primary, null)

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.pink_primary_dark, null))
    }

    private fun setCyan() {

        notebook.notebookColor = NotebookColor.CYAN

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_cyan_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.cyan_primary, null)

//            viewModel.notebook.value?.notebookColor = NotebookColor.CYAN

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.cyan_primary_dark, null))

    }
}