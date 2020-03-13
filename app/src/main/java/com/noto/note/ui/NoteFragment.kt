package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.noto.R
import com.noto.databinding.FragmentNoteBinding
import com.noto.network.Repos
import com.noto.note.model.NotebookColor
import com.noto.note.viewModel.NoteViewModel
import com.noto.note.viewModel.NoteViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentNoteBinding

    private var noteId = 0L

    private var notebookId = 0L

    private var notebookColor = NotebookColor.GRAY

    private var notebookTitle = ""

    private val viewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory(Repos.noteRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        requireArguments().let { args ->
            noteId = args.getLong("note_id")
            notebookId = args.getLong("notebook_id")
            notebookTitle = args.getString("notebook_title") ?: ""
            notebookColor = args.get("notebook_color") as NotebookColor
        }

        when (notebookColor) {
            NotebookColor.GRAY -> setGray()
            NotebookColor.BLUE -> setBlue()
            NotebookColor.PINK -> setPink()
            NotebookColor.CYAN -> setCyan()
        }

        binding.tb.let { tb ->
            tb.title = notebookTitle
        }

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
