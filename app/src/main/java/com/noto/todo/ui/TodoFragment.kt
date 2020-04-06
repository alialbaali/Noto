package com.noto.todo.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentTodoBinding
import com.noto.network.Repos
import com.noto.todo.adapter.SubTodoRVAdapter
import com.noto.todo.model.SubTodo
import com.noto.todo.viewModel.TodoViewModel
import com.noto.todo.viewModel.TodoViewModelFactory

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

    private val viewModel by viewModels<TodoViewModel> {
        TodoViewModelFactory(Repos.todoRepository, Repos.subTodoRepository)
    }

    private val imm by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val adapter by lazy {
        SubTodoRVAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getTodoById(args.todolistId, args.todoId)

        viewModel.getSubTodos(args.todoId)

        viewModel.subTodo.value = SubTodo(todoId = args.todoId)

        viewModel.notoColor.value = args.notoColor

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
            rv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter

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

        viewModel.todo.observe(viewLifecycleOwner, Observer {
            if (it.todoTitle.isBlank()) {
                binding.title.requestFocus()
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        })

        binding.addSubTodo.setOnClickListener {
            binding.subTodo.requestFocus()
            imm.showSoftInput(binding.subTodo, InputMethodManager.SHOW_FORCED)
        }

        binding.done.setOnClickListener {
            if (viewModel.subTodo.value?.subTodoTitle?.isNotBlank() == true) {
                imm.hideSoftInputFromWindow(
                    binding.subTodo.windowToken,
                    InputMethodManager.SHOW_FORCED
                )
                viewModel.saveSubTodo()
                binding.subTodo.setText("")
                viewModel.subTodo.value = SubTodo(todoId = args.todoId)
            }
        }

        return binding.root
    }
}
