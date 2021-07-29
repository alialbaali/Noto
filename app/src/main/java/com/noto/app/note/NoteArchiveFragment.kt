package com.noto.app.note

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
import com.noto.app.databinding.NoteArchiveFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.library.LibraryAdapter
import com.noto.app.library.LibraryViewModel
import com.noto.app.library.NoteItemClickListener
import com.noto.app.util.colorResource
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteArchiveFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteArchiveFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteArchiveFragmentBinding.inflate(inflater, container, false).withBinding {

        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val rvAdapter = LibraryAdapter(object : NoteItemClickListener {

            override fun onClick(note: Note) = findNavController().navigate(NoteArchiveFragmentDirections.actionArchiveFragmentToNotoFragment(args.libraryId, note.id))

            override fun onLongClick(note: Note) = findNavController().navigate(NoteArchiveFragmentDirections.actionArchiveFragmentToNotoDialogFragment(args.libraryId, note.id))

            override fun toggleNotoStar(note: Note) {
                viewModel.toggleNoteStar(note)
            }

        })

        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.notes
            .onEach {
                val notesCount = if (it.size == 1) " Archived Noto" else " Archived Notos"
                tvLibraryNotoCount.text = it.size.toString().plus(notesCount)
                rvAdapter.submitList(it)
            }
            .launchIn(lifecycleScope)

        viewModel.library
            .onEach {
                val color = colorResource(it.color.toResource())
                tb.navigationIcon?.mutate()?.setTint(color)
                ivLibraryIcon.imageTintList = colorStateResource(it.color.toResource())
                tvLibraryNotoCount.setTextColor(color)
                tvLibraryTitle.setTextColor(color)
                tvLibraryTitle.text = "${it.title} ${getString(R.string.archived_notes)}"
            }
            .launchIn(lifecycleScope)

    }

}