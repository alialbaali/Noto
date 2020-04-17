package com.noto.todo.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.R
import com.noto.databinding.FragmentTodolistBinding
import com.noto.todo.adapter.NavigateToTodo
import com.noto.todo.adapter.TodolistRVAdapter
import com.noto.todo.model.Todo
import com.noto.todo.viewModel.TodolistViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import com.noto.util.setList
import com.noto.util.setStatusBarColor
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TodolistFragment : Fragment(), NavigateToTodo {

    private val binding by lazy {
        FragmentTodolistBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val rvAdapter by lazy {
        TodolistRVAdapter(viewModel, args.notoColor, this)
    }

    private val args by navArgs<TodolistFragmentArgs>()

    private val viewModel by viewModel<TodolistViewModel>()

    private val imm by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val rvLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setNotoColor()

        viewModel.todo.postValue(Todo(todoListId = args.todolistId))

        viewModel.getTodos(args.todolistId)

        binding.newTodoBtn.setOnClickListener {
            viewModel.insertTodo()
            viewModel.todo.postValue(Todo(todoListId = args.todolistId))
        }

        binding.newTodoBtn.isEnabled = false
        binding.newTodoEt.addTextChangedListener {
            binding.newTodoBtn.isEnabled = !it.toString().isBlank()
        }

        binding.newTodo.setOnClickListener {
            binding.newTodoEt.requestFocus()
            imm.showSoftInput(binding.newTodoEt, InputMethodManager.SHOW_IMPLICIT)
        }

        // RV
        binding.rv.let { rv ->

            rv.adapter = rvAdapter

            rv.layoutManager = rvLayoutManager

            viewModel.todos.observe(viewLifecycleOwner, Observer {
                binding.rv.setList(it, rvAdapter, binding.emptyTodolist)
            })

        }

        binding.ctb.title = args.todolistTitle
//        binding.ctb.setFontFamily()

        binding.tb.let { tb ->

            tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(
                    binding.newTodoEt.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                this.findNavController().navigateUp()
            }

//            binding.abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//                if (verticalOffset == 0) {
//                    binding.tb.title = ""
//                } else {
//                    binding.tb.title = args.todolistTitle
//                }
//            })
            tb.setOnMenuItemClickListener {

                when (it.itemId) {

//                    R.id.style -> {
//                        NotoDialog(requireContext(), viewModel)
//                    }

                    R.id.delete -> {
                        this.findNavController().navigateUp()
                        viewModel.deleteTodolist(args.todolistId)
                    }
                }
                true
            }
        }

        return binding.root
    }

    private fun setNotoColor() {
        val colorPrimary = args.notoColor.getColorPrimary(requireContext())
        val colorOnPrimary = args.notoColor.getColorOnPrimary(requireContext())

        with(binding) {

            requireActivity().setStatusBarColor(args.notoColor)
            cool.setBackgroundColor(colorPrimary)
            tb.setBackgroundColor(colorPrimary)
            ctb.setBackgroundColor(colorPrimary)
            ctb.setContentScrimColor(colorPrimary)
            newTodo.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
            newTodoBtn.imageTintList = ColorStateList.valueOf(colorOnPrimary)
            val drawable = resources.getDrawable(R.drawable.ripple_btn, null)
            val rippleDrawable = RippleDrawable(ColorStateList.valueOf(colorPrimary), drawable, drawable)
            newTodoBtn.background = rippleDrawable

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
