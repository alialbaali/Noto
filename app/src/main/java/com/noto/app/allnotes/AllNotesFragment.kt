package com.noto.app.allnotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.AllNotesFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.library.noteItem
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllNotesFragment : Fragment() {

    private val viewModel by viewModel<AllNotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = AllNotesFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun AllNotesFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToSelectLibraryDialogFragment(libraryId = 0))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    viewModel.toggleIsSearchEnabled()
                    true
                }
                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment())
        }
    }

    private fun AllNotesFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        bab.setRoundedCorners()
        context?.let { context ->
            val backgroundColor = context.attributeColoResource(R.attr.notoBackgroundColor)
            bab.navigationIcon?.mutate()?.setTint(backgroundColor)
            bab.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
        }

        combine(
            viewModel.notes,
            viewModel.notesVisibility,
            viewModel.font,
            etSearch.textAsFlow()
                .onStart { emit(etSearch.text) }
                .filterNotNull()
                .map { it.trim() },
        ) { notes, notesVisibility, font, searchTerm ->
            setupNotes(notes.map { it.mapValues { it.value.filterContent(searchTerm) } }, notesVisibility, font)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled ->
                if (isSearchEnabled)
                    enableSearch()
                else
                    disableSearch()
            }
            .launchIn(lifecycleScope)

        viewModel.isCollapseToolbar
            .onEach { isCollapseToolbar -> abl.setExpanded(!isCollapseToolbar, false) }
            .launchIn(lifecycleScope)

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.LibraryId)
            ?.observe(viewLifecycleOwner) { libraryId ->
                if (libraryId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        navController?.currentBackStackEntry?.savedStateHandle?.remove<Long>(Constants.LibraryId)
                        navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(libraryId))
                    }
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun AllNotesFragmentBinding.setupNotes(
        state: UiState<Map<Library, List<NoteWithLabels>>>,
        notesVisibility: Map<Library, Boolean>,
        font: Font,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                rv.withModels {
                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        } else {
                            notes.filterValues { it.isNotEmpty() }.forEach { (library, notes) ->
                                val isVisible = notesVisibility[library] ?: true

                                headerItem {
                                    id("library ${library.id}")
                                    title(library.getTitle(context))
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForLibrary(library.id) }
                                }

                                if (isVisible)
                                    notes.forEach { entry ->
                                        noteItem {
                                            id(entry.first.id)
                                            note(entry.first)
                                            font(font)
                                            labels(entry.second)
                                            color(library.color)
                                            previewSize(library.notePreviewSize)
                                            isShowCreationDate(library.isShowNoteCreationDate)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(
                                                            entry.first.libraryId,
                                                            entry.first.id
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteDialogFragment(
                                                            entry.first.libraryId,
                                                            entry.first.id,
                                                            R.id.libraryFragment
                                                        )
                                                    )
                                                true
                                            }
                                            onDragHandleTouchListener { _, _ -> false }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun AllNotesFragmentBinding.enableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
            duration = 250
        }
        tilSearch.isVisible = true
        rv.startAnimation(rvAnimation)
        etSearch.requestFocus()
        etSearch.showKeyboardUsingImm()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            disableSearch()
            if (isEnabled) {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    private fun AllNotesFragmentBinding.disableSearch() {
        if (tilSearch.isVisible) {
            val rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
                duration = 250
            }
            tilSearch.isVisible = false
            rv.startAnimation(rvAnimation)
        }
        etSearch.text = null
        activity?.hideKeyboard(root)
    }
}