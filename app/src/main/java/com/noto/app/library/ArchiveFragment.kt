package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.FragmentArchiveBinding
import com.noto.app.util.colorResource
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource
import com.noto.domain.model.Note
import org.koin.android.viewmodel.ext.android.viewModel

class ArchiveFragment : Fragment() {

    private lateinit var binding: FragmentArchiveBinding

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<ArchiveFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentArchiveBinding.inflate(inflater, container, false)

        viewModel.getArchivedNotes(args.libraryId)
        viewModel.getLibrary(args.libraryId)

        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        with(binding.rv) {

            val rvAdapter = LibraryRVAdapter(object : NotoItemClickListener {

                override fun onClick(note: Note) {
                    findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(note.id))
                }

                override fun onLongClick(note: Note) {
                    findNavController().navigate(ArchiveFragmentDirections.actionArchiveFragmentToArchiveDialogFragment(note.id))
                }

                override fun toggleNotoStar(note: Note) {
                    viewModel.toggleNoteStar(note)
                }

            })

            binding.rv.adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.notos.observe(viewLifecycleOwner) { notos ->
                binding.tvLibraryNotoCount.text = notos.size.toString().plus(if (notos.size == 1) " Archived Noto" else " Archived Notos")
                rvAdapter.submitList(notos)
            }

        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            if (args.libraryId != 0L) {
                val color = colorResource(library.notoColor.toResource())
                binding.tb.navigationIcon?.mutate()?.setTint(color)
                binding.ivLibraryIcon.imageTintList = colorStateResource(library.notoColor.toResource())
                binding.tvLibraryNotoCount.setTextColor(color)
                binding.tvLibraryTitle.setTextColor(color)
                binding.tvLibraryTitle.text = "${library.libraryTitle} ${getString(R.string.archived_notos)}"
            }
        }

        return binding.root
    }

}