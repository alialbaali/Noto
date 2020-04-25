package com.noto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.R
import com.noto.adapter.LabelListRVAdapter
import com.noto.databinding.FragmentListLabelBinding
import com.noto.domain.Label
import com.noto.viewModel.LabelListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LabelListFragment : Fragment() {

    private val binding by lazy { FragmentListLabelBinding.inflate(layoutInflater).also { it.lifecycleOwner = this } }

    private val viewModel by viewModel<LabelListViewModel>()

    private val rvLayoutManager by lazy { LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) }

    private val rvAdapter by lazy { LabelListRVAdapter(viewModel) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding.fab.imageTintList = resources.getColorStateList(R.color.colorPrimary, null)

        binding.rv.adapter = rvAdapter
        binding.rv.layoutManager = rvLayoutManager

        viewModel.labels.observe(viewLifecycleOwner, Observer {
            it?.let {
                rvAdapter.submitList(it)
            }
        })

        binding.fab.setOnClickListener {
            LabelDialog(requireContext(), viewModel, Label())
        }

        binding.tb.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }

        binding.fab.imageTintList = resources.getColorStateList(R.color.colorBackground, null)

        return binding.root
    }
}