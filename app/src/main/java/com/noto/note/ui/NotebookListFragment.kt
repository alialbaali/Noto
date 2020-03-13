package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

    // Notebook List RV Adapter
    private lateinit var adapter: NotebookListRVAdapter

    private val viewModel by viewModels<NotebookListViewModel> {
        NotebookListViewModelFactory(Repos.notebookRepository)
    }

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
        }

        return binding.root
    }

    override fun navigate(notebook: Notebook) {
        this.findNavController().navigate(
            NotebookListFragmentDirections.actionNotebookListFragmentToNotebookFragment(
                notebook.notebookId,
                notebook.notebookTitle,
                notebook.notebookColor
            )
        )
    }

}
