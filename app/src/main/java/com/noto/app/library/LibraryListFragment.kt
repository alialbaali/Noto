package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.LibraryItemTouchHelper
import com.noto.app.R
import com.noto.app.databinding.FragmentListLibraryBinding
import com.noto.app.util.LayoutManager
import com.noto.app.util.colorResource
import com.noto.app.util.drawableResource
import com.noto.domain.model.Library
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class LibraryListFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentListLibraryBinding

    private val viewModel by viewModel<LibraryListViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentListLibraryBinding.inflate(inflater, container, false)

        with(binding) {
            fab.setOnClickListener {
                findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToNewLibraryDialogFragment())
            }
        }

        bab()
        rv()

        return binding.root
    }

    private fun bab() = with(binding.bab) {
        navigationIcon?.mutate()?.setTint(colorResource(R.color.colorPrimary))

        setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archived_notos -> {
                    findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToArchiveFragment())
                    true
                }

                R.id.view -> {

                    when (viewModel.layoutManager.value) {
                        LayoutManager.Linear -> viewModel.setLayoutManager(LayoutManager.Grid)
                        LayoutManager.Grid -> viewModel.setLayoutManager(LayoutManager.Linear)
                    }

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

    private fun rv() = with(binding.rv) {

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
            Timber.i("$value")
            value?.let {

                when (value) {
                    LayoutManager.Linear -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_grid_outline)
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    LayoutManager.Grid -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_agenda_outline)
                        layoutManager = GridLayoutManager(requireContext(), 2)
                    }
                }

                visibility = View.INVISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }

        }

        viewModel.libraries.observe(viewLifecycleOwner) {
            rvAdapter.submitList(it)

            binding.tvLibraryNotoCount.text = "${it.size} ${getString(R.string.libraries)}"

        }
    }
}