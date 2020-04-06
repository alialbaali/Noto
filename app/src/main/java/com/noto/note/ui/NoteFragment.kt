package com.noto.note.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    private val binding by lazy {
        FragmentNoteBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val viewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory(Repos.noteRepository)
    }

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                this.findNavController().navigateUp()
                viewModel.saveNote()
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

        binding.body.requestFocus()
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )

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
