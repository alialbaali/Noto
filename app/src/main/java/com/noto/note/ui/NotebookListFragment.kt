package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.noto.NotoDialog
import com.noto.R
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.databinding.FragmentNotebookListBinding
import com.noto.note.adapter.NavigateToNotebook
import com.noto.note.adapter.NotebookItemTouchHelperCallback
import com.noto.note.adapter.NotebookListRVAdapter
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.util.setFontFamily
import org.koin.android.viewmodel.ext.android.viewModel

class NotebookListFragment : Fragment(), NavigateToNotebook {

    // Binding
    private val binding by lazy {
        FragmentNotebookListBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val viewModel by viewModel<NotebookListViewModel>()

    private val rvLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private val rvAdapter by lazy {
        NotebookListRVAdapter(viewModel, this)
    }

    private val bs by lazy {
        BottomSheetBehavior.from(binding.bs)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bs.state = BottomSheetBehavior.STATE_HIDDEN

        requireActivity().window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        binding.ctb.setFontFamily()

        binding.tb.setOnMenuItemClickListener {
            if (bs.state == BottomSheetBehavior.STATE_EXPANDED){
                bs.state = BottomSheetBehavior.STATE_HIDDEN
            }

            when (it.itemId) {

                R.id.create -> NotoDialog(requireContext(), viewModel)

                R.id.sort -> {
                    bs.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            true
        }

        with(binding.rv) {

            adapter = rvAdapter
            layoutManager = rvLayoutManager

            NotebookItemTouchHelperCallback(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            viewModel.notebooks.observe(viewLifecycleOwner, Observer {
                it.let {
                    rvAdapter.submitList(it)
                }
            })
            viewModel.sortMethod.observe(viewLifecycleOwner, Observer {
                when (it){
                    SortMethod.Alphabetically -> binding.alphabetically.isChecked = true
                    SortMethod.Custom -> binding.custom.isChecked = true
                    SortMethod.CreationDate -> binding.creationDate.isChecked = true
                    SortMethod.ModificationDate -> binding.modificationDate.isChecked = true
                }
            })

            viewModel.sortType.observe(viewLifecycleOwner, Observer {
                when (it){
                    SortType.ASC -> binding.sortType.isChecked = true
                    SortType.DESC -> binding.sortType.isChecked = false
                }
            })
        }

        binding.creationDate.setOnClickListener {
            bs.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.updateSortMethod(SortMethod.CreationDate)
        }
        binding.alphabetically.setOnClickListener {
            bs.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.updateSortMethod(SortMethod.Alphabetically)
        }
        binding.custom.setOnClickListener {
            bs.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.updateSortMethod(SortMethod.Custom)
        }
        binding.modificationDate.setOnClickListener {
            bs.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.updateSortMethod(SortMethod.ModificationDate)
        }
        binding.sortType.setOnClickListener {
            bs.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.updateSortType()
        }
        return binding.root
    }

    override fun navigate(notebook: Notebook) {

        this.findNavController().navigate(
            NotebookListFragmentDirections.actionNotebookListFragmentToNotebookFragment(
                notebook.notebookId,
                notebook.notebookTitle,
                notebook.notoColor
            )
        )
    }
}
