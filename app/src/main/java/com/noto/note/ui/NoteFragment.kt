package com.noto.note.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.R
import com.noto.databinding.FragmentNoteBinding
import com.noto.note.viewModel.NoteViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import com.noto.util.setStatusBarColor
import org.koin.android.viewmodel.ext.android.viewModel

class NoteFragment : Fragment() {

    // Binding
    private val binding by lazy {
        FragmentNoteBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val viewModel by viewModel<NoteViewModel>()

    private val args by navArgs<NoteFragmentArgs>()

    private val imm by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setNotoColor()

        viewModel.getNoteById(args.notebookId, args.noteId)

        binding.tb.let { tb ->
            tb.title = args.notebookTitle

            tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(
                    binding.body.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
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

        viewModel.note.observe(viewLifecycleOwner, Observer {
            if (it.noteBody.isBlank()) {
                binding.body.requestFocus()
                imm.showSoftInput(binding.body, InputMethodManager.SHOW_IMPLICIT)
            } else {
                binding.root.clearFocus()
            }
        })

        return binding.root
    }


    private fun setNotoColor() {
        val colorPrimary = args.notoColor.getColorPrimary(binding.root.context)
        val colorOnPrimary = args.notoColor.getColorOnPrimary(binding.root.context)

        requireActivity().setStatusBarColor(args.notoColor)

        with(binding) {
            clBackLayer.backgroundTintList = ColorStateList.valueOf(colorPrimary)
            tb.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        }
    }

}
