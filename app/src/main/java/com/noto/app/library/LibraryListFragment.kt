package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.LibraryListFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.util.LayoutManager
import com.noto.app.util.drawableResource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryListFragment : BaseDialogFragment() {

    private lateinit var binding: LibraryListFragmentBinding

    private val viewModel by viewModel<LibraryListViewModel>()

    private val adapter by lazy { LibraryListAdapter(libraryItemClickListener) }

    private val libraryItemClickListener by lazy {
        object : LibraryItemClickListener {
            override fun onClick(library: Library) = findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryFragment(library.id))
            override fun onLongClick(library: Library) = findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryDialogFragment(library.id))
            override fun countLibraryNotos(library: Library): Int = viewModel.countNotos(library.id)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LibraryListFragmentBinding.inflate(inflater, container, false)


        setupListeners()
        collectState()
        binding.rv.adapter = adapter

        return binding.root
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToNewLibraryDialogFragment())
        }
        binding.bab.setOnMenuItemClickListener { menuItem ->
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
                else -> false
            }
        }
    }

    private fun collectState() {
        val layoutManagerMenuItem = binding.bab.menu.findItem(R.id.view)

        viewModel.layoutManager
            .onEach {
                when (it) {
                    LayoutManager.Linear -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_grid_outline)
                        binding.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    LayoutManager.Grid -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_agenda_outline)
                        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
                    }
                }
                binding.rv.visibility = View.INVISIBLE
                binding.rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                binding.rv.visibility = View.VISIBLE
                binding.rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
            }
            .launchIn(lifecycleScope)

        viewModel.libraries
            .onEach {
                adapter.submitList(it)
                binding.tvLibraryNotoCount.text = "${it.size} ${getString(R.string.libraries)}"
            }
            .launchIn(lifecycleScope)

    }

}