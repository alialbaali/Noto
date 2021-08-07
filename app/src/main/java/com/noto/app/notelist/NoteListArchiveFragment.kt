package com.noto.app.notelist

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
import com.noto.app.databinding.NoteListArchiveFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListArchiveFragment : Fragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListArchiveFragmentArgs>()

    private val noteItemClickListener by lazy {
        object : NoteListAdapter.NoteItemClickListener {
            override fun onClick(note: Note) = findNavController()
                .navigate(NoteListArchiveFragmentDirections.actionArchiveFragmentToNotoFragment(args.libraryId, note.id))

            override fun onLongClick(note: Note) =
                findNavController()
                    .navigate(
                        NoteListArchiveFragmentDirections.actionArchiveFragmentToNotoDialogFragment(
                            args.libraryId,
                            note.id,
                            R.id.archiveFragment
                        )
                    )
        }
    }

    private val adapter = NoteListAdapter(noteItemClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteListArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupRV()
            setupState()
        }

    private fun NoteListArchiveFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun NoteListArchiveFragmentBinding.setupRV() {
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun NoteListArchiveFragmentBinding.setupState() {
        val layoutItems = listOf(tvLibraryNotesCount, rv)

        viewModel.archivedNotes
            .onEach { archivedNotes -> setupArchivedNotes(archivedNotes, layoutItems) }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .launchIn(lifecycleScope)

        viewModel.layoutManager
            .onEach { layoutManager -> setupLayoutManger(layoutManager) }
            .launchIn(lifecycleScope)
    }

    private fun NoteListArchiveFragmentBinding.setupLayoutManger(layoutManager: LayoutManager) = when (layoutManager) {
        LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun NoteListArchiveFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())
        tb.navigationIcon?.mutate()?.setTint(color)
        tvLibraryNotesCount.setTextColor(color)
        tb.title = "${library.title} ${getString(R.string.archived_notes).replaceFirstChar { it.lowercase() }}"
        tb.setTitleTextColor(color)
    }

    private fun NoteListArchiveFragmentBinding.setupArchivedNotes(archivedNotes: List<Note>, layoutItems: List<View>) {
        if (archivedNotes.isEmpty()) {
            layoutItems.forEach { it.visibility = View.GONE }
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            tvPlaceHolder.visibility = View.GONE
            layoutItems.forEach { it.visibility = View.VISIBLE }
            adapter.submitList(archivedNotes)
            tvLibraryNotesCount.text = archivedNotes.size.toCountText(
                resources.stringResource(R.string.note),
                resources.stringResource(R.string.notes)
            )
        }
    }
}