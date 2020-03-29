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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.noto.R
import com.noto.databinding.FragmentNotebookListBinding
import com.noto.network.Repos
import com.noto.note.adapter.NavigateToNotebook
import com.noto.note.adapter.NotebookItemTouchHelperCallback
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

    private lateinit var exFabNewNotebook: ExtendedFloatingActionButton

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

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = NotebookListRVAdapter(this, viewModel)
            rv.adapter = adapter

            NotebookItemTouchHelperCallback(adapter).let {
                ItemTouchHelper(it).attachToRecyclerView(rv)
            }

            // RV Layout Manger
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            viewModel.notebooks.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        }

        exFabNewNotebook =
            activity?.findViewById(R.id.exFab_new_notebook) as ExtendedFloatingActionButton

        exFabNewNotebook.setOnClickListener {
            NotebookDialog(
                context!!,
                Notebook()
            ).apply {
                this.dialogBinding.createBtn.setOnClickListener {

                    when {
                        this.notebook.notebookTitle.isBlank() -> {

                            this.dialogBinding.til.error =
                                "Notebook with the same title already exists!"
                            this.dialogBinding.til.counterTextColor =
                                ColorStateList.valueOf(Color.RED)

                        }
                        viewModel.notebooks.value?.any { it.notebookTitle == this.notebook.notebookTitle }!! -> {

                            this.dialogBinding.til.error =
                                "Notebook with the same title already exists!"
                            this.dialogBinding.til.counterTextColor =
                                ColorStateList.valueOf(Color.RED)

                        }
                        else -> {
                            viewModel.saveNotebook(this.notebook)
                            this.dismiss()
                        }
                    }
                }
                create()
                show()

            }
        }


        return binding.root
    }

    override fun navigate(notebook: Notebook) {
//        val extras = FragmentNavigatorExtras(binding.rv to "my_transition")

        this.findNavController().navigate(
            NotebookListFragmentDirections.actionNotebookListFragmentToNotebookFragment(
                notebook.notebookId,
                notebook.notebookTitle,
                notebook.notoColor
            )
        )
    }

//    override fun onDrag(notebook: Notebook, target: Notebook) {
//        if (notebook.notebookPosition < target.notebookPosition) {
//            for (i in notebook.notebookPosition until target.notebookPosition) {
//                Timber.i("UPDATING")
//                val list = viewModel.get.value!!.toMutableList()
//
//                list[notebook.notebookPosition] = list[target.notebookPosition].also {
//                    list[target.notebookPosition] = list[notebook.notebookPosition]
//                }
//            }
//
//            } else {
////            for (i in fromPosition downTo toPosition + 1) {
////                Collections.swap(mItems, i, i - 1)
////            }
//            }
//            adapter.notifyItemMoved(notebook.notebookPosition, target.notebookPosition)
//        }
//
//    override fun onSwipe(notebook: Notebook) {
//        viewModel.deleteNotebook(notebook.notebookId)
//    }
//
//    override fun clearView() {
//        viewModel.update(viewModel.get.value!!)
//    }
}
