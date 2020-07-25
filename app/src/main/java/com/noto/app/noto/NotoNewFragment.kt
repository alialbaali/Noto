package com.noto.app.noto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.databinding.FragmentNewNotoBinding

class NotoNewFragment : Fragment(), BlockListener {

    private val binding by lazy {
        FragmentNewNotoBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val adapter by lazy { NotoRVAdapter(this) }

    private val layoutManager by lazy { LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) }

    private val list = mutableListOf<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding.rvNotoBody.adapter = adapter
        binding.rvNotoBody.layoutManager = layoutManager

        list.add("Hello World")

        adapter.submitList(list)



        return binding.root
    }


    override fun onClick(adapterPosition: Int) {
        val viewHolder = binding.rvNotoBody.findViewHolderForAdapterPosition(adapterPosition + 1) as NotoRVAdapter.StringViewHolder?
        if (viewHolder != null) {
            viewHolder.binding.editTextTextPersonName.requestFocus()
        } else {
            list.add("")
            adapter.notifyItemInserted(list.size)
            binding.rvNotoBody.smoothScrollToPosition(list.size)
            binding.rvNotoBody.post {
                val viewHolder2 = binding.rvNotoBody.findViewHolderForAdapterPosition(list.size - 1) as NotoRVAdapter.StringViewHolder?
                viewHolder2?.binding?.editTextTextPersonName?.requestFocus()
            }
        }
    }

    override fun onBack(adapterPosition: Int) {
//        val viewHolder = binding.rvNotoBody.findViewHolderForAdapterPosition(adapterPosition + 1) as NotoRVAdapter.StringViewHolder?
//        if (viewHolder != null) {
//            viewHolder.binding.editTextTextPersonName.requestFocus()
//        } else {
//            list.removeAt(adapterPosition + 1)
//            adapter.notifyItemRemoved(adapterPosition)
//            binding.rvNotoBody.post {
//                val viewHolder2 = binding.rvNotoBody.findViewHolderForAdapterPosition(list.size - 1) as NotoRVAdapter.StringViewHolder?
//                viewHolder2?.binding?.editTextTextPersonName?.requestFocus()
//            }
//        }
    }

}