package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.ArchiveFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.util.colorResource
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource
import com.noto.app.util.withBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArchiveFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<ArchiveFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ArchiveFragmentBinding.inflate(inflater, container, false).withBinding {

        viewModel.getArchivedNotes(args.libraryId)
        viewModel.getLibrary(args.libraryId)

        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val rvAdapter = LibraryAdapter(object : NotoItemClickListener {

            override fun onClick(note: Note) = findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(note.id))

            override fun onLongClick(note: Note) = findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(note.id))

            override fun toggleNotoStar(note: Note) {
                viewModel.toggleNoteStar(note)
            }

        })

        rv.adapter = rvAdapter
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.notos.observe(viewLifecycleOwner) { notos ->
            val notesCount = if (notos.size == 1) " Archived Noto" else " Archived Notos"
            tvLibraryNotoCount.text = notos.size.toString().plus(notesCount)
            rvAdapter.submitList(notos)
        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            if (args.libraryId != 0L) {
                val color = colorResource(library.color.toResource())
                tb.navigationIcon?.mutate()?.setTint(color)
                ivLibraryIcon.imageTintList = colorStateResource(library.color.toResource())
                tvLibraryNotoCount.setTextColor(color)
                tvLibraryTitle.setTextColor(color)
                tvLibraryTitle.text = "${library.title} ${getString(R.string.archived_notes)}"
            }
        }

    }

}