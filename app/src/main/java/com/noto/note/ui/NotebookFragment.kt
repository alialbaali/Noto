package com.noto.note.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.databinding.FragmentNotebookBinding
import com.noto.note.adapter.NavigateToNote
import com.noto.note.adapter.NotebookRVAdapter
import com.noto.note.model.Note
import com.noto.note.viewModel.NotebookViewModel
import com.noto.util.*
import org.koin.android.viewmodel.ext.android.viewModel

class NotebookFragment : Fragment(), NavigateToNote {

    private val binding by lazy {
        FragmentNotebookBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val rvAdapter by lazy {
        NotebookRVAdapter(viewModel, args.notoColor, this)
    }

    private val rvLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private val args by navArgs<NotebookFragmentArgs>()

    private val viewModel by viewModel<NotebookViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setNotoColor()

        viewModel.getNotes(args.notebookId)

        // RV
        binding.rv.let { rv ->
            rv.adapter = rvAdapter
            rv.layoutManager = rvLayoutManager

            viewModel.notes.observe(viewLifecycleOwner, Observer {
                rv.setList(it, rvAdapter, binding.emptyNotebook)
            })

        }

//        binding.ctb.setFontFamily()
        binding.ctb.title = args.notebookTitle

        binding.fab.setOnClickListener {
            this.findNavController().navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    0L,
                    args.notebookId,
                    args.notebookTitle,
                    args.notoColor
                )
            )
        }

        binding.tb.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }

        return binding.root
    }

    private fun setNotoColor() {
        val colorPrimary = args.notoColor.getColorPrimary(requireContext())
        val colorOnPrimary = args.notoColor.getColorOnPrimary(requireContext())

        with(binding) {
            requireActivity().setStatusBarColor(args.notoColor)
            tb.setBackgroundColor(colorPrimary)
            ctb.backgroundTintList = ColorStateList.valueOf(colorPrimary)
            ctb.setContentScrimColor(colorPrimary)
            fab.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
        }
    }

    override fun navigate(id: Long) {
        this.findNavController()
            .navigate(
                NotebookFragmentDirections.actionNotebookFragmentToNoteFragment(
                    id,
                    args.notebookId,
                    args.notebookTitle,
                    args.notoColor
                )
            )
    }
}

