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
import com.noto.app.R
import com.noto.app.databinding.NoteListArchiveFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.util.colorResource
import com.noto.app.util.toResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListArchiveFragment : Fragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListArchiveFragmentArgs>()

    private val adapter by lazy { NoteListAdapter(noteItemClickListener) }

    private val noteItemClickListener by lazy {
        object : NoteListAdapter.NoteItemClickListener {
            override fun onClick(note: Note) = findNavController().navigate(NoteListArchiveFragmentDirections.actionArchiveFragmentToNotoFragment(args.libraryId, note.id))
            override fun onLongClick(note: Note) = findNavController().navigate(NoteListArchiveFragmentDirections.actionArchiveFragmentToNotoDialogFragment(args.libraryId, note.id))
            override fun toggleNotoStar(note: Note) {
                viewModel.toggleNoteStar(note)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteListArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
        setupListeners()
        setupRV()
        collectState()
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

    private fun NoteListArchiveFragmentBinding.collectState() {
        viewModel.archivedNotes
            .onEach {
                val notesCount = if (it.size == 1) " Archived Noto" else " Archived Notos"
                tvLibraryNotesCount.text = it.size.toString().plus(notesCount)
                adapter.submitList(it)
            }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach {
                val color = resources.colorResource(it.color.toResource())
                tb.navigationIcon?.mutate()?.setTint(color)
                tvLibraryNotesCount.setTextColor(color)
                tb.title = "${it.title} ${getString(R.string.archived_notes)}"
                tb.setTitleTextColor(color)
            }
            .launchIn(lifecycleScope)
    }
}