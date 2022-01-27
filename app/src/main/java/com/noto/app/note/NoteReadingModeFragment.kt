package com.noto.app.note

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.databinding.NoteReadingModeFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.label.labelItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteReadingModeFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteReadingModeFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReadingModeFragmentBinding.inflate(inflater, container, false).withBinding {
            setupFadeTransition()
            setupState()
            setupListeners()
        }

    private fun NoteReadingModeFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
    }

    private fun NoteReadingModeFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = HorizontalListItemAnimator()
        abl.bringToFront()

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.note,
            viewModel.font,
        ) { note, font -> setupNote(note, font) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.library,
            viewModel.labels,
        ) { library, labels ->
            rv.withModels {
                labels.filterValues { it }.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(library.color)
                        onClickListener { _ -> }
                        onLongClickListener { _ -> false }
                    }
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.isCollapseToolbar
            .onEach { isCollapseToolbar -> abl.setExpanded(!isCollapseToolbar, false) }
            .launchIn(lifecycleScope)
    }

    private fun NoteReadingModeFragmentBinding.setupNote(note: Note, font: Font) {
        tvNoteTitle.text = note.title
        tvNoteBody.text = note.body
        context?.let { context ->
            tvCreatedAt.text = context.stringResource(R.string.created, note.creationDate.format(context))
            tvWordCount.text = context.pluralsResource(R.plurals.words_count, note.body.wordsCount, note.body.wordsCount).lowercase()
        }
        tvNoteTitle.isVisible = note.title.isNotBlank()
        tvNoteBody.isVisible = note.body.isNotBlank()
        tvNoteTitle.setBoldFont(font)
        tvNoteBody.setSemiboldFont(font)
    }

    private fun NoteReadingModeFragmentBinding.setupLibrary(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            ctb.title = folder.getTitle(context)
            ctb.setCollapsedTitleTextColor(colorStateList)
            ctb.setExpandedTitleTextColor(colorStateList)
            tvCreatedAt.setTextColor(color)
            tvWordCount.setTextColor(color)
            tvNoteTitle.setLinkTextColor(color)
            tvNoteBody.setLinkTextColor(color)
            tb.navigationIcon?.mutate()?.setTint(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nsv.verticalScrollbarThumbDrawable?.mutate()?.setTint(color)
            }
        }
    }
}