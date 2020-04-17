package com.noto.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.noto.NotoDialog
import com.noto.R
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.databinding.FragmentTodolistListBinding
import com.noto.todo.adapter.NavigateToTodolist
import com.noto.todo.adapter.TodolistItemTouchHelper
import com.noto.todo.adapter.TodolistListRVAdapter
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.util.setFontFamily
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TodolistListFragment : Fragment(), NavigateToTodolist {

    private val binding by lazy {
        FragmentTodolistListBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val rvAdapter by lazy {
        TodolistListRVAdapter(viewModel, this)
    }

    private val viewModel by viewModel<TodolistListViewModel>()

    private val rvLayoutManager by lazy {
        GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
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
            if (bs.state == BottomSheetBehavior.STATE_EXPANDED) {
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


        // RV
        binding.rv.let { rv ->

            // RV Adapter
            rv.adapter = rvAdapter

            // RV Layout Manger
            rv.layoutManager = rvLayoutManager

            TodolistItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(rv)
            }

            viewModel.todolists.observe(viewLifecycleOwner, Observer {
                it?.let { rvAdapter.submitList(it) }
            })
        }

        viewModel.sortMethod.observe(viewLifecycleOwner, Observer {
            when (it) {
                SortMethod.Alphabetically -> binding.alphabetically.isChecked = true
                SortMethod.Custom -> binding.custom.isChecked = true
                SortMethod.CreationDate -> binding.creationDate.isChecked = true
                SortMethod.ModificationDate -> binding.modificationDate.isChecked = true
            }
        })

        viewModel.sortType.observe(viewLifecycleOwner, Observer {
            when (it) {
                SortType.ASC -> binding.sortType.isChecked = true
                SortType.DESC -> binding.sortType.isChecked = false
            }
        })

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

    override fun navigate(todolist: Todolist) {
        this.findNavController().navigate(
            TodolistListFragmentDirections.actionTodolistListFragmentToTodolistFragment(
                todolist.todolistId,
                todolist.todolistTitle,
                todolist.notoColor
            )
        )
    }

}
