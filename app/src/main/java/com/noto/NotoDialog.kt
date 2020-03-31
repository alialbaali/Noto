package com.noto

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.noto.database.NotoColor
import com.noto.databinding.DialogNotoBinding
import com.noto.note.model.Notebook
import com.noto.todo.model.Todolist

class NotoDialog(context: Context, val notebook: Notebook?, val todolist: Todolist?) :
    AlertDialog(context) {

    private val resources = context.resources

    internal val dialogBinding = DialogNotoBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dialogBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DialogAnimation)
//        window?.setDimAmount(0f)


        when (notebook?.notoColor) {
            NotoColor.GRAY -> setGray()
            NotoColor.BLUE -> setBlue()
            NotoColor.PINK -> setPink()
            NotoColor.CYAN -> setCyan()
        }

        when (todolist?.notoColor) {
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

        notebook?.notoColor = NotoColor.GRAY
        todolist?.notoColor = NotoColor.GRAY

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryGray))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))

    }

    private fun setBlue() {

        notebook?.notoColor = NotoColor.BLUE
        todolist?.notoColor = NotoColor.BLUE

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryBlue))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))

    }

    private fun setPink() {

        notebook?.notoColor = NotoColor.PINK
        todolist?.notoColor = NotoColor.PINK

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryPink))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))

    }

    private fun setCyan() {

        notebook?.notoColor = NotoColor.CYAN
        todolist?.notoColor = NotoColor.CYAN

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryCyan))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))

    }
}