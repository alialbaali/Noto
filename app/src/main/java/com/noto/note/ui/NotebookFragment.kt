package com.noto.note.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noto.R
import com.noto.databinding.FragmentNotebookBinding
import com.noto.note.model.NotebookColor

/**
 * A simple [Fragment] subclass.
 */
class NotebookFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // Binding
    private lateinit var binding: FragmentNotebookBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookBinding.inflate(inflater, container, false)

        val arguments = requireArguments()

        val id = arguments.getLong("id")
        val title = arguments.getString("title")
        val color = arguments.get("color") as NotebookColor


        // Binding
        binding.let {

            it.lifecycleOwner = this

            when (color) {
                NotebookColor.GRAY -> setGray()
                NotebookColor.BLUE -> setBlue()
                NotebookColor.PINK -> setPink()
                NotebookColor.CYAN -> setCyan()
            }

        }

        // Coordinator Layout
        binding.cool.let { cool ->

        }

        // RV
        binding.rv.let { rv ->

        }

        // FAB
        binding.fab.let { fab ->

        }

        // Collapsing Toolbar
        binding.ctb.let { ctb ->
            ctb.title = title
        }

        return binding.root
    }


    private fun setGray() {

        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_gray_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_gray_color,
                    null
                )
            )
            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_gray_color))
            it.fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setBlue() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_blue_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_blue_color,
                    null
                )
            )
            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_blue_color))
            it.fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setPink() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_pink_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_pink_color,
                    null
                )
            )
            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_pink_color))
            it.fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    private fun setCyan() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.notebook_item_background_cyan_color, null)

            it.cool.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.notebook_item_background_cyan_color,
                    null
                )
            )
            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.notebook_fab_background_cyan_color))
            it.fab.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

}

