package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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

        binding.viewModel = viewModel

        arguments?.let { args ->
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

        viewModel.getNoteById(notebookId, noteId)

        binding.tb.let { tb ->
            tb.title = notebookTitle

            tb.setNavigationOnClickListener {
                viewModel.saveNote()
                this.findNavController().navigateUp()
            }

            tb.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_note -> {
                        viewModel.deleteNote()
                        this.findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }

        // Configure Back Dispatcher to save the note
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this@NoteFragment.findNavController().navigateUp()
            viewModel.saveNote()
        }.isEnabled = true

        // A workarount to one way data binding
        viewModel.note.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.title.setText(it.noteTitle)
                binding.body.setText(it.noteBody)
            }
        })

        return binding.root
    }

    private fun setCyan() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.cyan_primary,
                    null
                )
            )
        }
    }

    private fun setPink() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.pink_primary,
                    null
                )
            )
        }
    }

    private fun setBlue() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.blue_primary,
                    null
                )
            )
        }
    }

    private fun setGray() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.gray_primary,
                    null
                )
            )
        }
    }

}
