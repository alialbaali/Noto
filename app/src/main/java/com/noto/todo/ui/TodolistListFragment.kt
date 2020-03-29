package com.noto.todo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentTodolistListBinding
import com.noto.todo.adapter.TodoListListRVAdapter

/**
 * A simple [Fragment] subclass.
 */
class TodolistListFragment : Fragment() {

    private lateinit var binding: FragmentTodolistListBinding

    private lateinit var adapter: TodoListListRVAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodolistListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_medium))

        }

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = TodoListListRVAdapter(requireContext())
            rv.adapter = adapter

            // RV Layout Manger
            rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//            viewModel.notebooks.observe(viewLifecycleOwner, Observer {
//                it?.let {
//                    adapter.submitList(it)
//                }
//            })
        }


        return binding.root

    }

}
