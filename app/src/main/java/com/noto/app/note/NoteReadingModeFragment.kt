package com.noto.app.note

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteReadingModeFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteReadingModeFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteReadingModeFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReadingModeFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun NoteReadingModeFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun NoteReadingModeFragmentBinding.setupState() {
        nsv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

        viewModel.state
            .onEach { state ->
                setupLibrary(state.library)
                setupNote(state.note, state.font)
            }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("SetTextI18n")
    private fun NoteReadingModeFragmentBinding.setupNote(note: Note, font: Font) {
        tvNoteTitle.text = note.title
        tvNoteBody.text = note.body
        tvCreatedAt.text = "${resources.stringResource(R.string.created)} ${note.formatCreationDate()}"
        tvWordCount.text = note.countWords(resources.stringResource(R.string.word), resources.stringResource(R.string.words))
        tvNoteTitle.isVisible = note.title.isNotBlank()
        tvNoteBody.isVisible = note.body.isNotBlank()

        when (font) {
            Font.Nunito -> {
                tvNoteTitle.typeface = requireContext().tryLoadingFontResource(R.font.nunito_bold)
                tvNoteBody.typeface = requireContext().tryLoadingFontResource(R.font.nunito_semibold)
            }
            Font.Monospace -> {
                tvNoteTitle.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                tvNoteBody.typeface = Typeface.MONOSPACE
            }
        }
    }

    private fun NoteReadingModeFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())
        tb.title = library.title
        tb.setTitleTextColor(color)
        tvCreatedAt.setTextColor(color)
        tvWordCount.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            nsv.verticalScrollbarThumbDrawable?.mutate()?.setTint(color)
        }
    }
}