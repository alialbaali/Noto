package com.noto.todo.ui

import android.content.res.ColorStateList
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
import com.noto.note.model.Notebook
import com.noto.todo.adapter.NavigateToTodolist
import com.noto.todo.adapter.TodolistListRVAdapter
import com.noto.todo.model.Todolist
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

        binding.tb.menu.findItem(R.id.create).setOnMenuItemClickListener {

            NotoDialog(requireContext(), null, Todolist()).apply {
                this.todolist!!

                this.dialogBinding.et.hint = "Todolist title"

                this.dialogBinding.createBtn.setOnClickListener {

                    when {
                        viewModel.todolists.value!!.any {
                            it.todolistTitle ==
                                    dialogBinding.et.text.toString()
                        } -> {

                            this.dialogBinding.til.error =
                                "Notebook with the same title already exists!"

                        }
                        this.dialogBinding.et.text.toString().isBlank() -> {

                            this.dialogBinding.til.error =
                                "Notebook title can't be empty"

                            this.dialogBinding.til.counterTextColor =
                                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))

                        }
                        else -> {
                            this.todolist.todolistTitle = this.dialogBinding.et.text.toString()
                            viewModel.saveTodolist(this.todolist)
                            this.dismiss()
                        }
                    }
                }
                this.create()
                this.show()
            }

            true
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
