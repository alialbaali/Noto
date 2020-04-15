package com.noto.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.NotoDialog
import com.noto.R
import com.noto.databinding.FragmentTodolistListBinding
import com.noto.todo.adapter.NavigateToTodolist
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
        TodolistListRVAdapter(this)
    }

    private val viewModel by viewModel<TodolistListViewModel>()

    private val rvLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        binding.ctb.setFontFamily()

        binding.tb.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.create -> {
                    NotoDialog(requireContext(), viewModel)
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

            viewModel.todolists.observe(viewLifecycleOwner, Observer {
                it?.let {
                    rvAdapter.submitList(it)
                }
            })
        }

        return binding.root
    }

    override fun navigate(todolist: Todolist) {
        this.findNavController()
            .navigate(
                TodolistListFragmentDirections.actionTodolistListFragmentToTodolistFragment(
                    todolist.todolistId,
                    todolist.todolistTitle,
                    todolist.notoColor
                )
            )
    }

}
