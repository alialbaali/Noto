package com.noto.app.notelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.databinding.NoteListFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListFragment : Fragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListFragmentArgs>()

    private val adapter by lazy { NoteListAdapter(noteItemClickListener) }

    private val noteItemClickListener by lazy {
        object : NoteListAdapter.NoteItemClickListener {
            override fun onClick(note: Note) = findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoFragment(note.libraryId, note.id))
            override fun onLongClick(note: Note) = findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoDialogFragment(note.libraryId, note.id, R.id.libraryFragment))
        }
    }

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteListFragmentBinding.inflate(inflater, container, false).withBinding {
            collectState()
            setupListeners()
            setupRV()
        }

    private fun NoteListFragmentBinding.collectState() {
        viewModel.library
            .filterNotNull()
            .onEach {
                setLibraryColors(it.color)
                tb.title = it.title
            }
            .launchIn(lifecycleScope)

        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout_manager)

        viewModel.layoutManager
            .onEach {
                when (it) {
                    LayoutManager.Linear -> {
                        layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_dashboard_24)
                        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    LayoutManager.Grid -> {
                        layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_agenda_24)
                        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                }

                rv.visibility = View.INVISIBLE
                rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                rv.visibility = View.VISIBLE
                rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }
            .launchIn(lifecycleScope)

        val layoutItems = listOf(tvLibraryNotesCount, rv)

        viewModel.notes
            .onEach {
                tvLibraryNotesCount.text = it.size.toCountText(resources.stringResource(R.string.note), resources.stringResource(R.string.notes))
                if (it.isEmpty()) {

                    if (tilSearch.isVisible)
                        tvPlaceHolder.text = resources.stringResource(R.string.no_note_matches_search_term)

                    layoutItems.forEach { it.visibility = View.GONE }
                    tvPlaceHolder.visibility = View.VISIBLE
                } else {
                    layoutItems.forEach { it.visibility = View.VISIBLE }
                    tvPlaceHolder.visibility = View.GONE
                    adapter.submitList(it)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteListFragmentBinding.setLibraryColors(notoColor: NotoColor) {

        val color = resources.colorResource(notoColor.toResource())
        val colorStateList = resources.colorStateResource(notoColor.toResource())

        tb.setTitleTextColor(color)
        tvLibraryNotesCount.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        fab.backgroundTintList = colorStateList
    }

    private fun NoteListFragmentBinding.setupListeners() {

        fab.setOnClickListener {
            findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId))
        }
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archived_notos -> {
                    findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToArchiveFragment(args.libraryId))
                    true
                }

                R.id.layout_manager -> {
                    when (viewModel.layoutManager.value) {
                        LayoutManager.Linear -> viewModel.updateLayoutManager(LayoutManager.Grid)
                        LayoutManager.Grid -> viewModel.updateLayoutManager(LayoutManager.Linear)
                    }
                    true
                }

                R.id.search -> {
                    val searchEt = tilSearch
                    searchEt.isVisible = !searchEt.isVisible
                    if (searchEt.isVisible)
                        enableSearch()
                    else
                        disableSearch()
                    true
                }
                else -> false
            }
        }

        etSearch.doAfterTextChanged {
            viewModel.searchNotes(it.toString())
        }

    }

    private fun NoteListFragmentBinding.enableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
            duration = 250
        }
        rv.startAnimation(rvAnimation)
        etSearch.requestFocus()
        imm.showKeyboard()

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                disableSearch()
                if (isEnabled) isEnabled = false
            }
    }

    private fun NoteListFragmentBinding.disableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
            duration = 250
        }
        tilSearch.isVisible = false
        rv.startAnimation(rvAnimation)
        imm.hideKeyboard(etSearch.windowToken)
    }

    private fun NoteListFragmentBinding.setupRV() {
        rv.adapter = adapter
    }

}
