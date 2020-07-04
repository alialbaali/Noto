package com.noto.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.BaseBottomSheetDialogFragment
import com.noto.LibraryItemTouchHelper
import com.noto.R
import com.noto.databinding.FragmentListLibraryBinding
import com.noto.domain.model.Library
import org.koin.android.viewmodel.ext.android.viewModel

class LibraryListFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentListLibraryBinding

    private val viewModel by viewModel<LibraryListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentListLibraryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@LibraryListFragment
            viewModel = this@LibraryListFragment.viewModel
        }

        with(binding.fab) {

            imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

            binding.fab.setOnClickListener {
                findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryDialogFragment())
            }
        }

        with(binding.bab) {

            navigationIcon?.mutate()?.setTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))

            setNavigationOnClickListener {

            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.archived_notos -> {
                        findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToArchiveFragment())
                        true
                    }
                    else -> false
                }
            }
        }

        with(binding.rv) {

            val rvAdapter = LibraryListRVAdapter(object : LibraryItemClickListener {
                override fun onClick(library: Library) {
                    findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryFragment(library.libraryId))
                }
            })

            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            LibraryItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            viewModel.libraries.observe(viewLifecycleOwner) {
                rvAdapter.submitList(it)
            }

        }

        return binding.root
    }
}