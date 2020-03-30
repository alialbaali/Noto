package com.noto.note.ui

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.noto.databinding.FragmentNotebookListBinding
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNotebook
import com.noto.note.adapter.NotebookListRVAdapter
import com.noto.note.model.Notebook
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.note.viewModel.NotebookListViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class NotebookListFragment : Fragment(), NavigateToNotebook {

    // Binding
    private lateinit var binding: FragmentNotebookListBinding

    private val viewModel by viewModels<NotebookListViewModel> {
        NotebookListViewModelFactory(Repos.notebookRepository)
    }

    private lateinit var adapter: NotebookListRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotebookListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))

        }

        binding.tb.menu.findItem(R.id.create).setOnMenuItemClickListener {

            NotoDialog(requireContext(), Notebook(), null).apply {
                this.notebook!!

                this.dialogBinding.et.hint = "Notebook title"

                this.dialogBinding.createBtn.setOnClickListener {

                    when {
                        viewModel.notebooks.value!!.any {
                            it.notebookTitle ==
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
                            this.notebook.notebookTitle = this.dialogBinding.et.text.toString()
                            viewModel.saveNotebook(this.notebook)
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
            adapter = NotebookListRVAdapter(requireContext(), this, viewModel)
            rv.adapter = adapter

//            NotebookItemTouchHelperCallback(adapter).let {
//                ItemTouchHelper(it).attachToRecyclerView(rv)
//            }

            // RV Layout Manger
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            viewModel.notebooks.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        }

        return binding.root
    }

    override fun navigate(notebook: Notebook) {

        this.findNavController().navigate(
            NotebookListFragmentDirections.actionNotebookListFragmentToNotebookFragment(
                notebook.notebookId,
                notebook.notebookTitle,
                notebook.notoColor
            )
        )
    }
}
