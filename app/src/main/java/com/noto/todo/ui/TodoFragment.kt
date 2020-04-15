package com.noto.todo.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentTodoBinding
import com.noto.note.model.Notebook
import com.noto.todo.adapter.SubTodoRVAdapter
import com.noto.todo.model.SubTodo
import com.noto.todo.viewModel.TodoViewModel
import com.noto.util.getColorOnPrimary
import com.noto.util.getColorPrimary
import com.noto.util.setStatusBarColor
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class TodoFragment : Fragment() {

    private val binding by lazy {
        FragmentTodoBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val args by navArgs<TodoFragmentArgs>()

    private val viewModel by viewModel<TodoViewModel>()

    private val imm by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val adapter by lazy {
        SubTodoRVAdapter(viewModel, args.notoColor)
    }

    private val rvLayoutManger by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setNotoColor()

        viewModel.getTodoById(args.todolistId, args.todoId)

        viewModel.getSubTodos(args.todoId)

        viewModel.subTodo.value = SubTodo(todoId = args.todoId)

        binding.tb.let { tb ->
            tb.title = args.todolistTitle

            tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.title.windowToken, 0)
                this.findNavController().navigateUp()
                viewModel.saveTodo()
                viewModel.updateSubTodos()
            }

            tb.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_note -> {
//                        viewModel.deleteNote()
                        this.findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.rv.let { rv ->

            rv.adapter = adapter

            rv.layoutManager = rvLayoutManger

            viewModel.subTodos.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        }

        // Configure Back Dispatcher to save the note
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this@TodoFragment.findNavController().navigateUp()
            viewModel.saveTodo()
            viewModel.updateSubTodos()
        }.isEnabled = true

        binding.addSubTodo.setOnClickListener {
            binding.subTodo.requestFocus()
            imm.showSoftInput(binding.subTodo, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.subTodo.addTextChangedListener {
            binding.done.isVisible = !it.toString().isBlank()
        }

        binding.done.setOnClickListener {
            viewModel.saveSubTodo()
            viewModel.subTodo.value = SubTodo(todoId = args.todoId)
        }

        return binding.root
    }

    private fun setNotoColor() {
        val colorPrimary = args.notoColor.getColorPrimary(binding.root.context)
        val colorOnPrimary = args.notoColor.getColorOnPrimary(binding.root.context)

        requireActivity().setStatusBarColor(args.notoColor)
        binding.clBackLayer.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        binding.tb.backgroundTintList = ColorStateList.valueOf(colorPrimary)
        binding.check.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
        binding.star.backgroundTintList = ColorStateList.valueOf(colorOnPrimary)
    }
}
