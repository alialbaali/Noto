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
import com.noto.database.NotoColor
import com.noto.databinding.DialogNotebookBinding
import com.noto.note.model.Notebook

class NotebookDialog(context: Context, val notebook: Notebook) : AlertDialog(context) {
    private val resources = context.resources

    internal val dialogBinding = DialogNotebookBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dialogBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DialogAnimation)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialogBinding.et.requestFocus()

        dialogBinding.notebook = notebook

        when (notebook.notoColor) {
            NotoColor.GRAY -> setGray()
            NotoColor.BLUE -> setBlue()
            NotoColor.PINK -> setPink()
            NotoColor.CYAN -> setCyan()
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

        notebook.notoColor = NotoColor.GRAY

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.colorPrimaryGray, null)

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_gray_drawable, null)

        dialogBinding.et.setBackgroundColor(
            resources.getColor(R.color.colorPrimaryGray, null)
        )

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
    }

    private fun setBlue() {

        notebook.notoColor = NotoColor.BLUE

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_blue_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.colorPrimaryBlue, null)

        dialogBinding.et.setBackgroundColor(
            resources.getColor(R.color.colorPrimaryBlue, null)
        )

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
    }

    private fun setPink() {

        notebook.notoColor = NotoColor.PINK

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_pink_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.colorPrimaryPink, null)

        dialogBinding.et.setBackgroundColor(
            resources.getColor(R.color.colorPrimaryPink, null)
        )

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
    }

    private fun setCyan() {

        notebook.notoColor = NotoColor.CYAN

        dialogBinding.root.background =
            resources.getDrawable(R.drawable.dialog_background_cyan_drawable, null)

        dialogBinding.til.boxBackgroundColor =
            resources.getColor(R.color.colorPrimaryCyan, null)

        dialogBinding.et.setBackgroundColor(
            resources.getColor(R.color.colorPrimaryCyan, null)
        )

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))

    }
}