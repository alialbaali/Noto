package com.noto.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.NotoDialog
import com.noto.R
import com.noto.databinding.FragmentTodolistListBinding
import com.noto.network.Repos
import com.noto.todo.adapter.NavigateToTodolist
import com.noto.todo.adapter.TodolistListRVAdapter
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.todo.viewModel.TodolistListViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class TodolistListFragment : Fragment(), NavigateToTodolist {

    private val binding by lazy {
        FragmentTodolistListBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val adapter by lazy {
        TodolistListRVAdapter(this)
    }

    private val viewModel by viewModels<TodolistListViewModel> {
        TodolistListViewModelFactory(Repos.todolistRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.roboto_bold
                )
            )

            ctb.setExpandedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.roboto_medium
                )
            )
        }

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
            rv.adapter = adapter

            // RV Layout Manger
            rv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.todolists.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
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
