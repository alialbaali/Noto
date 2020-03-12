package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noto.R
import com.noto.databinding.FragmentNoteBinding
import com.noto.note.model.NotebookColor

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        val arguments = requireArguments()

        val id = arguments.getLong("id")
        val notebookId = arguments.getLong("notebook_id")
        val title = arguments.getString("title")

        when (arguments.get("color") as NotebookColor) {
            NotebookColor.GRAY -> setGray()
            NotebookColor.BLUE -> setBlue()
            NotebookColor.PINK -> setPink()
            NotebookColor.CYAN -> setCyan()
        }

        binding.tb.let { tb ->
            tb.title = title
        }

        binding.title.setText("This is a note title")
        binding.body.setText("This is a note body")

        return binding.root
    }

    private fun setCyan() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
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
        }
    }

    private fun setPink() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
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
        }
    }

    private fun setBlue() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
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
        }
    }

    private fun setGray() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
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
        }
    }

}
