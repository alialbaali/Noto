package com.noto.app.allnotes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.MenuItemCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.headerItem
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.AllNotesFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.folder.NoteItemModel
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
        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

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
            if (viewModel.isSearchEnabled.value) {
                viewModel.disableSearch()
            } else {
                navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToMainFragment(exit = true))
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun AllNotesFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        tvNotesCount.animationInterpolator = DefaultInterpolator()
        tvNotesCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        val layoutManager = rv.layoutManager as LinearLayoutManager
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        combine(
            viewModel.notes,
            viewModel.notesVisibility,
            viewModel.font,
            viewModel.searchTerm,
        ) { notes, notesVisibility, font, searchTerm ->
            setupNotes(notes, notesVisibility, font, searchTerm)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled -> if (isSearchEnabled) enableSearch() else disableSearch() }
            .launchIn(lifecycleScope)

        etSearch.textAsFlow()
            .asSearchFlow()
            .onEach { searchTerm -> viewModel.setSearchTerm(searchTerm) }
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

        root.keyboardVisibilityAsFlow()
            .onEach { isVisible ->
                fab.isVisible = !isVisible
                bab.isVisible = !isVisible
                tilSearch.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    anchorId = if (isVisible) View.NO_ID else fab.id
                    gravity = if (isVisible) Gravity.BOTTOM else Gravity.TOP
                }
            }
            .launchIn(lifecycleScope)

        savedStateHandle?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner) { folderId ->
                if (folderId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        savedStateHandle.remove<Long>(Constants.FolderId)
                        navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(
                            folderId,
                            selectedNoteIds = longArrayOf()
                        ))
                    }
                }
            }

        if (isCurrentLocaleArabic()) {
            tvNotesCount.isVisible = false
            tvNotesCountRtl.isVisible = true
        } else {
            tvNotesCount.isVisible = true
            tvNotesCountRtl.isVisible = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun AllNotesFragmentBinding.setupNotes(
        state: UiState<Map<Folder, List<NoteItemModel>>>,
        notesVisibility: Map<Folder, Boolean>,
        font: Font,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)

                rv.withModels {

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        } else {
                            notes.forEach { (folder, notes) ->
                                val isVisible = notesVisibility[folder] ?: true
                                val noteIds = notes.map { it.note.id }.toLongArray()

                                headerItem {
                                    id("folder ${folder.id}")
                                    title(folder.getTitle(context))
                                    color(folder.color)
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForFolder(folder.id) }
                                    onCreateClickListener { _ ->
                                        navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(
                                            folder.id,
                                            selectedNoteIds = longArrayOf()
                                        ))
                                    }
                                    onLongClickListener { _ ->
                                        navController?.navigateSafely(AllNotesFragmentDirections.actionAllNotesFragmentToFolderFragment(folder.id))
                                        true
                                    }
                                }

                                if (isVisible)
                                    notes.forEach { model ->
                                        noteItem {
                                            id(model.note.id)
                                            model(model)
                                            font(font)
                                            color(folder.color)
                                            searchTerm(searchTerm)
                                            previewSize(folder.notePreviewSize)
                                            isShowCreationDate(folder.isShowNoteCreationDate)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteFragment(
                                                            model.note.folderId,
                                                            noteId = model.note.id,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        AllNotesFragmentDirections.actionAllNotesFragmentToNoteDialogFragment(
                                                            model.note.folderId,
                                                            model.note.id,
                                                            R.id.folderFragment,
                                                            selectedNoteIds = noteIds,
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
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
    }

    private fun AllNotesFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }
}