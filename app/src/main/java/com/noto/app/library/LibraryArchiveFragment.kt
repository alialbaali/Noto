package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.databinding.LibraryArchiveFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.LayoutManager
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryArchiveFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryArchiveFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun LibraryArchiveFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun LibraryArchiveFragmentBinding.setupState() {
        val layoutItems = listOf(tvLibraryNotesCount, rv)

        viewModel.state
            .onEach { state ->
                setupLibrary(state.library)
                setupArchivedNotes(state.archivedNotes, state.font, layoutItems)
            }
            .distinctUntilChangedBy { state -> state.library.layoutManager }
            .onEach { state -> setupLayoutManger(state.library.layoutManager) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryArchiveFragmentBinding.setupLayoutManger(layoutManager: LayoutManager) = when (layoutManager) {
        LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun LibraryArchiveFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())
        tb.navigationIcon?.mutate()?.setTint(color)
        tvLibraryNotesCount.setTextColor(color)
        tb.title = library.getArchiveText(resources.stringResource(R.string.archive))
        tb.setTitleTextColor(color)
    }

    private fun LibraryArchiveFragmentBinding.setupArchivedNotes(archivedNotes: List<Note>, font: Font, layoutItems: List<View>) {
        if (archivedNotes.isEmpty()) {
            layoutItems.forEach { it.visibility = View.GONE }
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            tvPlaceHolder.visibility = View.GONE
            layoutItems.forEach { it.visibility = View.VISIBLE }
            rv.withModels {
                archivedNotes.forEach { archivedNote ->
                    noteItem {
                        id(archivedNote.id)
                        note(archivedNote)
                        font(font)
                        onClickListener { _ ->
                            findNavController()
                                .navigate(
                                    LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteFragment(
                                        archivedNote.libraryId,
                                        archivedNote.id
                                    )
                                )
                        }
                        onLongClickListener { _ ->
                            findNavController()
                                .navigate(
                                    LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteDialogFragment(
                                        archivedNote.libraryId,
                                        archivedNote.id,
                                        R.id.libraryArchiveFragment
                                    )
                                )
                            true
                        }
                    }
                }
            }
            tvLibraryNotesCount.text = archivedNotes.size.toCountText(
                resources.stringResource(R.string.note),
                resources.stringResource(R.string.notes)
            )
        }
    }
}