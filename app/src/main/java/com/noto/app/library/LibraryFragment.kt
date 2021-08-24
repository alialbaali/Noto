package com.noto.app.library

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.databinding.LibraryFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.LayoutManager
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun LibraryFragmentBinding.setupState() {
        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout_manager)
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
        val layoutItems = listOf(tvLibraryNotesCount, rv)

        viewModel.state
            .onEach { state ->
                setupLibraryColors(state.library.color)
                setupNotes(state.notes, state.font, layoutItems)
                tb.title = state.library.title
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val text = state.library.getArchiveText(resources.stringResource(R.string.archive))
                    archiveMenuItem.contentDescription = text
                    archiveMenuItem.tooltipText = text
                }
            }
            .distinctUntilChangedBy { state -> state.library.layoutManager }
            .onEach { state -> setupLayoutManager(state.library.layoutManager, layoutManagerMenuItem) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryFragmentBinding.setupListeners() {

        fab.setOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(args.libraryId))
        }
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archive -> setupArchivedNotesMenuItem()
                R.id.layout_manager -> setupLayoutManagerMenuItem()
                R.id.search -> setupSearchMenuItem()
                else -> false
            }
        }

        etSearch.doAfterTextChanged {
            viewModel.searchNotes(it.toString())
        }
    }

    private fun LibraryFragmentBinding.enableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
            duration = 250
        }
        rv.startAnimation(rvAnimation)
        etSearch.requestFocus()
        requireActivity().showKeyboard(root)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                disableSearch()
                if (isEnabled) isEnabled = false
            }
    }

    private fun LibraryFragmentBinding.disableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
            duration = 250
        }
        tilSearch.isVisible = false
        rv.startAnimation(rvAnimation)
        requireActivity().hideKeyboard(root)
    }

    private fun LibraryFragmentBinding.setupLayoutManager(layoutManager: LayoutManager, layoutManagerMenuItem: MenuItem) {
        val color = viewModel.state.value.library.color.toResource()
        val resource = resources.colorResource(color)
        when (layoutManager) {
            LayoutManager.Linear -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_dashboard_24)
                rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            LayoutManager.Grid -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_agenda_24)
                rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
        layoutManagerMenuItem.icon?.mutate()?.setTint(resource)

        rv.visibility = View.INVISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun LibraryFragmentBinding.setupNotes(notes: List<Note>, font: Font, layoutItems: List<View>) {
        tvLibraryNotesCount.text = notes.size.toCountText(resources.stringResource(R.string.note), resources.stringResource(R.string.notes))
        if (notes.isEmpty()) {
            if (tilSearch.isVisible)
                tvPlaceHolder.text = resources.stringResource(R.string.no_note_matches_search_term)
            layoutItems.forEach { it.visibility = View.GONE }
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            layoutItems.forEach { it.visibility = View.VISIBLE }
            tvPlaceHolder.visibility = View.GONE
            rv.withModels {
                notes.forEach { note ->
                    noteItem {
                        id(note.id)
                        note(note)
                        font(font)
                        onClickListener { _ ->
                            findNavController()
                                .navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(note.libraryId, note.id))
                        }
                        onLongClickListener { _ ->
                            findNavController()
                                .navigate(
                                    LibraryFragmentDirections.actionLibraryFragmentToNoteDialogFragment(
                                        note.libraryId,
                                        note.id,
                                        R.id.libraryFragment
                                    )
                                )
                            true
                        }
                    }
                }
            }
        }
    }

    private fun LibraryFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryArchiveFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupLayoutManagerMenuItem(): Boolean {
        when (viewModel.state.value.library.layoutManager) {
            LayoutManager.Linear -> {
                viewModel.updateLayoutManager(LayoutManager.Grid)
                root.snackbar(getString(R.string.layout_is_staggered_mode), anchorView = fab)
            }
            LayoutManager.Grid -> {
                viewModel.updateLayoutManager(LayoutManager.Linear)
                root.snackbar(getString(R.string.layout_is_list_mode), anchorView = fab)
            }
        }
        return true
    }

    private fun LibraryFragmentBinding.setupSearchMenuItem(): Boolean {
        val searchEt = tilSearch
        searchEt.isVisible = !searchEt.isVisible
        if (searchEt.isVisible)
            enableSearch()
        else
            disableSearch()
        return true
    }

    private fun LibraryFragmentBinding.setupLibraryColors(notoColor: NotoColor) {

        val color = resources.colorResource(notoColor.toResource())
        val colorStateList = resources.colorStateResource(notoColor.toResource())

        tb.setTitleTextColor(color)
        tvLibraryNotesCount.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        bab.navigationIcon?.mutate()?.setTint(color)
        fab.backgroundTintList = colorStateList
        bab.menu.forEach { it.icon?.mutate()?.setTint(color) }
        tilSearch.boxStrokeColor = color
        tilSearch.hintTextColor = colorStateList
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fab.outlineAmbientShadowColor = color
            fab.outlineSpotShadowColor = color
        }
    }
}
