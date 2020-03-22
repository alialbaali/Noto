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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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

        // Binding
        binding.let {

            it.lifecycleOwner = this

        }

        activity?.window?.statusBarColor = resources.getColor(R.color.bottom_nav_color, null)

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))

        }

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = NotebookListRVAdapter(this)
            rv.adapter = adapter

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

            val dialog = NotebookDialog(context!!).apply {
                this.notebook = Notebook()
                create()
                show()
            }

            dialog.dialogBinding.createBtn.setOnClickListener {

                when {
                    dialog.notebook.notebookTitle.isBlank() -> {

                        dialog.dialogBinding.til.error =
                            "Notebook with the same title already exists!"
                        dialog.dialogBinding.til.counterTextColor =
                            ColorStateList.valueOf(Color.RED)

                    }
                    viewModel.notebooks.value?.any { it.notebookTitle == dialog.notebook.notebookTitle }!! -> {

                        dialog.dialogBinding.til.error =
                            "Notebook with the same title already exists!"
                        dialog.dialogBinding.til.counterTextColor =
                            ColorStateList.valueOf(Color.RED)

                    }
                    else -> {
                        viewModel.saveNotebook(dialog.notebook)
                        dialog.dismiss()
                    }
                }

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
                notebook.notebookColor
            )
        )
    }
}
