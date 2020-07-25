package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.FragmentListLabelBinding
import com.noto.domain.model.Label
import com.noto.app.label.LabelListFragmentDirections
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LabelListFragment : Fragment() {

    private lateinit var binding: FragmentListLabelBinding

    private val viewModel by sharedViewModel<LabelViewModel>()

    private val rvLayoutManager by lazy { LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) }

    private val rvAdapter by lazy {

        LabelListRVAdapter(object : LabelListRVAdapter.LabelItemListener {

            override fun onClick(label: Label) {
                findNavController().navigate(LabelListFragmentDirections.actionLabelListFragmentToLabelDialogFragment(label.labelId))
            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentListLabelBinding.inflate(layoutInflater, container, false).apply {
            lifecycleOwner = this@LabelListFragment
        }

        binding.fab.imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

        binding.rv.adapter = rvAdapter

        binding.rv.layoutManager = rvLayoutManager

        viewModel.labels.observe(viewLifecycleOwner) { rvAdapter.submitList(it) }

        binding.fab.setOnClickListener { findNavController().navigate(LabelListFragmentDirections.actionLabelListFragmentToLabelDialogFragment()) }

        binding.tb.setNavigationOnClickListener { findNavController().navigateUp() }

        return binding.root
    }
}