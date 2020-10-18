package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.LibraryItemTouchHelper
import com.noto.app.R
import com.noto.app.databinding.FragmentListLibraryBinding
import com.noto.domain.model.Library
import org.koin.android.viewmodel.ext.android.viewModel

const val LINEAR_LAYOUT_MANAGER = 1
const val GRID_LAYOUT_MANAGER = 2
const val STAGGERED_LAYOUT_MANAGER = 3

class LibraryListFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentListLibraryBinding

    private val viewModel by viewModel<LibraryListViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentListLibraryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@LibraryListFragment
            viewModel = this@LibraryListFragment.viewModel
        }

        with(binding.fab) {

            imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

            binding.fab.setOnClickListener {
                findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToNewLibraryDialogFragment())
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

                    R.id.view -> {

                        val currentLayoutManager = viewModel.layoutManager.value

                        if (currentLayoutManager == LINEAR_LAYOUT_MANAGER) viewModel.setLayoutManager(GRID_LAYOUT_MANAGER)
                        else if (currentLayoutManager == GRID_LAYOUT_MANAGER) viewModel.setLayoutManager(LINEAR_LAYOUT_MANAGER)

                        true
                    }

                    R.id.theme -> {
                        findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToThemeDialogFragment())
                        true
                    }

//                    R.id.labels -> {
//                        findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLabelListFragment())
//                        true
//                    }

                    else -> false
                }
            }
        }

        with(binding.rv) {

            val rvAdapter = LibraryListRVAdapter(object : LibraryItemClickListener {

                override fun onClick(library: Library) = findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryFragment(library.libraryId))

                override fun onLongClick(library: Library) = findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryDialogFragment(library.libraryId))

                override fun countLibraryNotos(library: Library): Int = viewModel.countNotos(library.libraryId)

            })

            adapter = rvAdapter

//            LinearSnapHelper().attachToRecyclerView(this)

            LibraryItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            val layoutManagerMenuItem = binding.bab.menu.findItem(R.id.view)

            viewModel.layoutManager.observe(viewLifecycleOwner) { value ->

                if (value == LINEAR_LAYOUT_MANAGER) {
                    layoutManagerMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.view_grid_outline, null)
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                } else {
                    layoutManagerMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.view_agenda_outline, null)
                    layoutManager = GridLayoutManager(requireContext(), 2)
                }

                visibility = View.INVISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }

            viewModel.libraries.observe(viewLifecycleOwner) {
                rvAdapter.submitList(it)

                binding.tvLibraryNotoCount.text = "${it.size} ${getString(R.string.libraries)}"

            }

        }


        return binding.root
    }
}