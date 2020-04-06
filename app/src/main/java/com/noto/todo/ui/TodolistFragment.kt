package com.noto.todo.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.NotoDialog
import com.noto.R
import com.noto.database.NotoColor
import com.noto.databinding.FragmentTodolistBinding
import com.noto.network.Repos
import com.noto.todo.adapter.NavigateToTodo
import com.noto.todo.adapter.TodolistRVAdapter
import com.noto.todo.model.Todo
import com.noto.todo.model.Todolist
import com.noto.todo.viewModel.TodolistViewModel
import com.noto.todo.viewModel.TodolistViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class TodolistFragment : Fragment(), NavigateToTodo {

    private lateinit var binding: FragmentTodolistBinding

    private lateinit var adapter: TodolistRVAdapter

    private val args by navArgs<TodolistFragmentArgs>()

    private val viewModel by viewModels<TodolistViewModel> {
        TodolistViewModelFactory(Repos.todolistRepository, Repos.todoRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodolistBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        when (args.notoColor) {
            NotoColor.GRAY -> setGray()
            NotoColor.BLUE -> setBlue()
            NotoColor.PINK -> setPink()
            NotoColor.CYAN -> setCyan()
        }

        // RV
        binding.rv.let { rv ->

            viewModel.getTodos(args.todolistId)

            adapter = TodolistRVAdapter(args.notoColor, this)

            rv.adapter = adapter

            rv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            viewModel.todos.observe(viewLifecycleOwner, Observer {

                it?.let {

                    if (it.isEmpty()) {
                        rv.visibility = View.GONE
                        binding.emptyTodolist.visibility = View.VISIBLE
                    } else {

                        rv.visibility = View.VISIBLE
                        binding.emptyTodolist.visibility = View.GONE
                        adapter.submitList(it)
                    }

                }

            })

        }

        // Collapsing Toolbar
        binding.ctb.let { ctb ->

            ctb.title = args.todolistTitle

            ctb.setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    context!!,
                    R.font.roboto_bold
                )
            )

            ctb.setExpandedTitleTypeface(
                ResourcesCompat.getFont(
                    context!!,
                    R.font.roboto_medium
                )
            )
        }

        binding.fab.setOnClickListener {
            this.findNavController().navigate(
                TodolistFragmentDirections.actionTodolistFragmentToTodoFragment(
                    0L,
                    args.todolistId,
                    args.todolistTitle,
                    args.notoColor
                )
            )
        }

        binding.tb.let { tb ->

            tb.setNavigationOnClickListener {
                this.findNavController().navigateUp()
            }

            tb.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.style -> {
                        NotoDialog(
                            context!!,
                            null,
                            Todolist(args.todolistId, args.todolistTitle, args.notoColor)
                        ).apply {
                            this.todolist!!

                            dialogBinding.createBtn.text = resources.getString(R.string.update)

                            dialogBinding.et.setText(args.todolistTitle)

                            dialogBinding.et.setSelection(args.todolistTitle.length)

                            this.dialogBinding.et.hint = "Todolist title"

                            this.dialogBinding.createBtn.setOnClickListener {

                                when {
//                                    viewModel.notebooks.value!!.any {
//                                        it.notebookTitle ==
//                                                dialogBinding.et.text.toString()
//                                    } -> {
//
//                                        this.dialogBinding.til.error =
//                                            "Notebook with the same title already exists!"
//
//                                    }
                                    this.dialogBinding.et.text.toString().isBlank() -> {

                                        this.dialogBinding.til.error =
                                            "Todolist title can't be empty"

                                        this.dialogBinding.til.counterTextColor =
                                            ColorStateList.valueOf(
                                                resources.getColor(
                                                    R.color.colorOnPrimaryPink,
                                                    null
                                                )
                                            )

                                    }
                                    else -> {
                                        this.todolist.todolistTitle =
                                            this.dialogBinding.et.text.toString()
                                        viewModel.updateTodolist(this.todolist)
                                        this.dismiss()
                                    }
                                }
                            }
                            this.create()
                            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                            this.show()
                            dialogBinding.et.requestFocus()
                            this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        }
                        true
                    }

                    R.id.delete_notebook -> {
                        this.findNavController().navigateUp()
                        viewModel.deleteTodolist(args.todolistId)
                        true
                    }
                    else ->
                        false
                }
            }
        }
        return binding.root
    }


    private fun setGray() {

        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryGray, null)

            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryGray, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryGray,
                    null
                )
            )


            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryGray, null))
        }
    }

    private fun setBlue() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryBlue, null)

            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryBlue, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )

            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )

            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryBlue,
                    null
                )
            )


            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryBlue, null))
        }
    }

    private fun setPink() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryPink, null)


            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryPink, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryPink,
                    null
                )
            )

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryPink, null))
        }
    }

    private fun setCyan() {
        binding.let {

            activity?.window?.statusBarColor =
                resources.getColor(R.color.colorPrimaryCyan, null)


            it.root.setBackgroundColor(resources.getColor(R.color.colorPrimaryCyan, null))

            it.ctb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )
            it.ctb.setContentScrimColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )
            it.tb.setBackgroundColor(
                resources.getColor(
                    R.color.colorPrimaryCyan,
                    null
                )
            )

            it.fab.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.colorOnPrimaryCyan, null))
        }
    }

    override fun navigate(todo: Todo) {
        this.findNavController().navigate(
            TodolistFragmentDirections.actionTodolistFragmentToTodoFragment(
                todo.todoId,
                args.todolistId,
                args.todolistTitle,
                args.notoColor
            )
        )
    }
}
