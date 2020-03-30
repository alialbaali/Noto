package com.noto

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.view.isVisible
import com.noto.database.NotoColor
import com.noto.databinding.DialogNotebookBinding
import com.noto.note.model.Notebook
import com.noto.todo.model.Todolist

class NotoDialog(context: Context, val notebook: Notebook?, val todolist: Todolist?) :
    AlertDialog(context) {

    private val resources = context.resources

    internal var selected = 0

    internal val dialogBinding = DialogNotebookBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(dialogBinding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.DialogAnimation)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialogBinding.et.requestFocus()
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

//        dialogBinding.rbtnNotebook.setOnClickListener {
//            setNotebook()
//        }
//        dialogBinding.rbtnTodolist.setOnClickListener {
//            setTodolist()
//        }
    }

//    private fun setNotebook() {
//
////        selected = 0
//
////        dialogBinding.rbtnNotebook.let { rbtnNotebook ->
////
////            rbtnNotebook.setTextColor(Color.WHITE)
////
////            rbtnNotebook.compoundDrawableTintList =
////                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
////
////            rbtnNotebook.backgroundTintList =
////                when (notebook?.notoColor) {
////                    NotoColor.GRAY ->
////                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
////                    NotoColor.BLUE ->
////                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
////                    NotoColor.PINK ->
////                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
////                    NotoColor.CYAN ->
////                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
////                    else ->
////                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
////                }
////        }
////
////        dialogBinding.rbtnTodolist.let { rbtnTodolist ->
////
////            rbtnTodolist.compoundDrawableTintList =
////                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimary_800, null))
////
////            rbtnTodolist.setTextColor(Color.BLACK)
////
////            rbtnTodolist.backgroundTintList =
////                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
////
////        }
//    }
//
//    private fun setTodolist() {
//
//        selected = 1
//
//        dialogBinding.rbtnNotebook.let { rbtnNotebook ->
//
//            rbtnNotebook.compoundDrawableTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimary_800, null))
//
//
//            rbtnNotebook.setTextColor(Color.BLACK)
//
//            rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//        }
//
//
//        dialogBinding.rbtnTodolist.let { rbtnTodolist ->
//
//            rbtnTodolist.setTextColor(Color.WHITE)
//
//            rbtnTodolist.compoundDrawableTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//            rbtnTodolist.backgroundTintList =
//                when (todolist?.notoColor) {
//                    NotoColor.GRAY ->
//                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
//                    NotoColor.BLUE ->
//                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
//                    NotoColor.PINK ->
//                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
//                    NotoColor.CYAN ->
//                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
//                    else ->
//                        ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
//                }
//        }
//
//
//    }

    private fun setGray() {

        notebook?.notoColor = NotoColor.GRAY
        todolist?.notoColor = NotoColor.GRAY

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryGray))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))

//        if (selected == 0) {
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//        } else {
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//        }
    }

    private fun setBlue() {

        notebook?.notoColor = NotoColor.BLUE
        todolist?.notoColor = NotoColor.BLUE

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryBlue))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
//        if (selected == 0) {
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//        } else {
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//        }

    }

    private fun setPink() {

        notebook?.notoColor = NotoColor.PINK
        todolist?.notoColor = NotoColor.PINK

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryPink))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))

//        if (selected == 0) {
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//        } else {
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//        }
    }

    private fun setCyan() {

        notebook?.notoColor = NotoColor.CYAN
        todolist?.notoColor = NotoColor.CYAN

        dialogBinding.root.backgroundTintList =
            ColorStateList.valueOf(context.getColor(R.color.colorPrimaryCyan))

        dialogBinding.createBtn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))

//        if (selected == 0) {
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//
//        } else {
//            dialogBinding.rbtnTodolist.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
//            dialogBinding.rbtnNotebook.backgroundTintList =
//                ColorStateList.valueOf(resources.getColor(R.color.colorPrimary, null))
//        }

    }

}