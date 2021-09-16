package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import kotlinx.coroutines.flow.combine
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
        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .distinctUntilChangedBy { library -> library.layoutManager }
            .onEach { library -> setupLayoutManger(library.layoutManager) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.archivedNotes,
            viewModel.font,
            viewModel.library,
        ) { archivedNotes, font, library ->
            setupArchivedNotes(archivedNotes.sorted(library.sorting, library.sortingOrder), font, library)
        }.launchIn(lifecycleScope)
    }

    private fun LibraryArchiveFragmentBinding.setupLayoutManger(layoutManager: LayoutManager) {
        when (layoutManager) {
            LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun LibraryArchiveFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())
        tb.navigationIcon?.mutate()?.setTint(color)
        tb.title = library.getArchiveText(resources.stringResource(R.string.archive))
        tb.setTitleTextColor(color)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun LibraryArchiveFragmentBinding.setupArchivedNotes(archivedNotes: List<Note>, font: Font, library: Library) {
        if (archivedNotes.isEmpty()) {
            rv.visibility = View.GONE
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvPlaceHolder.visibility = View.GONE
            rv.withModels {

                val items = { items: List<Note> ->
                    items.forEach { archivedNote ->
                        noteItem {
                            id(archivedNote.id)
                            note(archivedNote)
                            font(font)
                            previewSize(library.notePreviewSize)
                            isShowCreationDate(library.isShowNoteCreationDate)
                            isManualSorting(false)
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
                            onDragHandleTouchListener { _, _ -> false }
                        }
                    }
                }

                val pinnedNotes = archivedNotes.filter { it.isPinned }
                val notPinnedNotes = archivedNotes.filterNot { it.isPinned }

                if (pinnedNotes.isNotEmpty()) {
                    headerItem {
                        id("pinned")
                        title(resources.stringResource(R.string.pinned))
                    }

                    items(pinnedNotes)

                    if (notPinnedNotes.isNotEmpty())
                        headerItem {
                            id("notes")
                            title(resources.stringResource(R.string.notes))
                        }
                }

                items(notPinnedNotes)
            }
        }
    }
}