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
import androidx.navigation.fragment.navArgs
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.FragmentNoteBinding
import com.noto.network.Repos
import com.noto.note.viewModel.NoteViewModel
import com.noto.note.viewModel.NoteViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class NoteFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentNoteBinding

    private val viewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory(Repos.noteRepository)
    }

    private val args by navArgs<NoteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel

        when (args.notoColor) {
            NotoColor.GRAY -> setGray()
            NotoColor.BLUE -> setBlue()
            NotoColor.PINK -> setPink()
            NotoColor.CYAN -> setCyan()
        }

        viewModel.getNoteById(args.notebookId, args.noteId)

        binding.tb.let { tb ->
            tb.title = args.notebookTitle

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

        // A workaround to one way data binding
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
                    R.color.colorPrimaryCyan,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )
        }
    }

    private fun setPink() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
        }
    }

    private fun setBlue() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )
        }
    }

    private fun setGray() {
        binding.let {
            it.clBackLayer.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
        }
    }

}
