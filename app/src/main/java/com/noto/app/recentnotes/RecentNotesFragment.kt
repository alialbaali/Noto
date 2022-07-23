package com.noto.app.recentnotes

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
import com.noto.app.databinding.RecentNotesFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.NotoColor
import com.noto.app.folder.noteItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecentNotesFragment : Fragment() {

    private val viewModel by viewModel<RecentNotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = RecentNotesFragmentBinding.inflate(inflater, container, false).withBinding {
        setupMixedTransitions()
        setupState()
        setupListeners()
    }

    private fun RecentNotesFragmentBinding.setupListeners() {
        tb.setOnClickListener {
            navController?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToMainFragment())
        }

        fab.setOnClickListener {
            navController
                ?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToSelectFolderDialogFragment(longArrayOf()))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToMainFragment())
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
            navController?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToMainFragment(exit = true))
        }
    }

    @OptIn(FlowPreview::class)
    private fun RecentNotesFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        tvNotesCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        tvNotesCount.animationInterpolator = DefaultInterpolator()
        val layoutManager = rv.layoutManager as LinearLayoutManager

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

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner) { folderId ->
                if (folderId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        navController?.currentBackStackEntry?.savedStateHandle?.remove<Long>(Constants.FolderId)
                        navController?.navigateSafely(RecentNotesFragmentDirections.actionRecentNotesFragmentToNoteFragment(
                            folderId))
                    }
                }
            }

        viewModel.language
            .onEach { language ->
                when (language) {
                    Language.Arabic -> {
                        tvNotesCount.isVisible = false
                        tvNotesCountRtl.isVisible = true
                    }
                    else -> {
                        tvNotesCount.isVisible = true
                        tvNotesCountRtl.isVisible = false
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun RecentNotesFragmentBinding.setupNotes(
        state: UiState<Map<LocalDate, List<NoteWithLabels>>>,
        notesVisibility: Map<LocalDate, Boolean>,
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
                            notes.forEach { (date, notes) ->
                                val isVisible = notesVisibility[date] ?: true

                                headerItem {
                                    id(date.dayOfYear)
                                    title(date.format())
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForDate(date) }
                                }

                                if (isVisible)
                                    notes.forEach { entry ->
                                        noteItem {
                                            id(entry.first.id)
                                            note(entry.first)
                                            font(font)
                                            labels(entry.second)
                                            color(NotoColor.Black)
                                            previewSize(15)
                                            isShowCreationDate(false)
                                            isShowAccessDate(true)
                                            isManualSorting(false)
                                            searchTerm(searchTerm)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        RecentNotesFragmentDirections.actionRecentNotesFragmentToNoteFragment(
                                                            entry.first.folderId,
                                                            entry.first.id
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        RecentNotesFragmentDirections.actionRecentNotesFragmentToNoteDialogFragment(
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

    private fun RecentNotesFragmentBinding.enableSearch() {
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            viewModel.disableSearch()
            if (isEnabled) isEnabled = false
        }
    }

    private fun RecentNotesFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }
}