package com.noto.note.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.databinding.FragmentNotebookListBinding
import com.noto.note.adapter.NotebookListRVAdapter
import com.noto.note.model.Notebook

/**
 * A simple [Fragment] subclass.
 */
class NotebookListFragment : Fragment() {

    // Binding
    private lateinit var binding: FragmentNotebookListBinding

    // Notebook List RV Adapter
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

        // Collapse Toolbar
        binding.ctb.let { ctb ->

            ctb.setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_bold))

            ctb.setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.roboto_medium))

        }

        // RV
        binding.rv.let { rv ->

            // RV Adapter
            adapter = NotebookListRVAdapter()
            rv.adapter = adapter

            // RV Layout Manger
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val list = mutableListOf<Notebook>()
            for (i in 1..100) {
                list.add(Notebook(title = "Notebook $i"))
            }
            adapter.submitList(list)
        }

        return binding.root
    }

}
