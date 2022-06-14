package com.noto.app.allnotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.AllNotesFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.folder.noteItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllNotesFragment : Fragment() {

    private val viewModel by viewModel<AllNotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = AllNotesFragmentBinding.inflate(inflater, container, false).withBinding {
        setupMixedTransitions()
        setupState()
        setupListeners()
    }

    private fun AllNotesFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToSelectFolderDialogFragment(longArrayOf()))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    if (viewModel.isSearchEnabled.value)
                        viewModel.disableSearch()
                    else
                        viewModel.enableSearch()
                    true
                }
                R.id.change_visibility -> {
                    if (viewModel.notesVisibility.value.any { it.value })
                        viewModel.collapseAll()
                    else
                        viewModel.expandAll()
                    true
                }
                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment(exit = true))
        }
    }

    @OptIn(FlowPreview::class)
    private fun AllNotesFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        tvNotesCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
        val layoutManager = rv.layoutManager as LinearLayoutManager

        combine(
            viewModel.notes,
            viewModel.notesVisibility,
            viewModel.font,
            viewModel.isSearchEnabled,
            viewModel.searchTerm,
        ) { notes, notesVisibility, font, isSearchEnabled, searchTerm ->
            setupNotes(notes, notesVisibility, font, isSearchEnabled, searchTerm)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled ->
                if (isSearchEnabled)
                    rv.smoothScrollToPosition(0)
            }
            .launchIn(lifecycleScope)

        val menuItem = bab.menu.findItem(R.id.change_visibility)
        val expandText = context?.stringResource(R.string.expand)
        val collapseText = context?.stringResource(R.string.collapse)

        viewModel.notesVisibility
            .onEach { visibility ->
                if (visibility.any { it.value }) {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_collapse_24)
                    menuItem.title = collapseText
                    MenuItemCompat.setContentDescription(menuItem, collapseText)
                } else {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_expand_24)
                    menuItem.title = expandText
                    MenuItemCompat.setContentDescription(menuItem, expandText)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.isRememberScrollingPosition,
            viewModel.scrollingPosition
        ) { isRememberScrollingPosition, scrollingPosition ->
            layoutManager.postOnAnimation {
                rv.post {
                    if (isRememberScrollingPosition) {
                        layoutManager.scrollToPosition(scrollingPosition)
                    }
                }
            }
        }.launchIn(lifecycleScope)

        rv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach {
                val scrollingPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (scrollingPosition != -1) viewModel.updateScrollingPosition(scrollingPosition)
            }
            .launchIn(lifecycleScope)

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner) { folderId ->
                if (folderId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        navController?.currentBackStackEntry?.savedStateHandle?.remove<Long>(Constants.FolderId)
                        navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(folderId))
                    }
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun AllNotesFragmentBinding.setupNotes(
        state: UiState<Map<Folder, List<NoteWithLabels>>>,
        notesVisibility: Map<Folder, Boolean>,
        font: Font,
        isSearchEnabled: Boolean,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)?.lowercase()

                rv.withModels {

                    if (isSearchEnabled) {
                        searchItem {
                            id("search")
                            searchTerm(searchTerm)
                            callback { searchTerm -> viewModel.setSearchTerm(searchTerm) }
                            onBind { _, view, _ ->
                                if (view.binding.etSearch.text.toString().isBlank()) {
                                    view.binding.etSearch.requestFocus()
                                    activity?.showKeyboard(view.binding.etSearch)
                                }
                            }
                            onUnbind { _, view ->
                                activity?.hideKeyboard(view.binding.etSearch)
                            }
                        }
                        if (activity?.onBackPressedDispatcher?.hasEnabledCallbacks() == false) {
                            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                                viewModel.disableSearch()
                                if (isEnabled) isEnabled = false
                            }
                        }
                    }

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        } else {
                            notes.forEach { (folder, notes) ->
                                val isVisible = notesVisibility[folder] ?: true

                                headerItem {
                                    id("folder ${folder.id}")
                                    title(folder.getTitle(context))
                                    color(folder.color)
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForFolder(folder.id) }
                                }

                                if (isVisible)
                                    notes.forEach { entry ->
                                        noteItem {
                                            id(entry.first.id)
                                            note(entry.first)
                                            font(font)
                                            labels(entry.second)
                                            color(folder.color)
                                            searchTerm(if (isSearchEnabled) searchTerm.trim() else null)
                                            previewSize(folder.notePreviewSize)
                                            isShowCreationDate(folder.isShowNoteCreationDate)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(
                                                            entry.first.folderId,
                                                            entry.first.id
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteDialogFragment(
                                                            entry.first.folderId,
                                                            entry.first.id,
                                                            R.id.folderFragment
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
}