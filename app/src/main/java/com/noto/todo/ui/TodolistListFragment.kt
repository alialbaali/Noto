package com.noto.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentTodolistListBinding
import com.noto.network.Repos
import com.noto.todo.adapter.NavigateToTodolist
import com.noto.todo.adapter.TodolistListRVAdapter
import com.noto.todo.viewModel.TodolistListViewModel
import com.noto.todo.viewModel.TodolistListViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class TodolistListFragment : Fragment(), NavigateToTodolist {

    private lateinit var binding: FragmentTodolistListBinding

    private lateinit var adapter: TodolistListRVAdapter

    private val viewModel by viewModels<TodolistListViewModel> {
        TodolistListViewModelFactory(Repos.todolistRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodolistListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

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

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = TodolistListRVAdapter(requireContext(), this)
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

    override fun navigate(todolistId: Long) {
        
    }

}
