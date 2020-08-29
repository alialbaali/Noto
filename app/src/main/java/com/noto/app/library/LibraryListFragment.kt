package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.LibraryItemTouchHelper
import com.noto.R
import com.noto.databinding.FragmentListLibraryBinding
import com.noto.domain.model.Library
import com.noto.app.library.LibraryListFragmentDirections
import org.koin.android.viewmodel.ext.android.viewModel

class LibraryListFragment : Fragment(), LibraryItemClickListener {

    // Binding
    private val binding by lazy {
        FragmentListLibraryBinding.inflate(layoutInflater, null, false).also {
            it.lifecycleOwner = this
        }
    }

    private val viewModel by viewModel<LibraryListViewModel>()

    private val rvLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private val rvAdapter by lazy {
        LibraryListRVAdapter(viewModel, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.colorSurface)

//        binding.tb.setOnMenuItemClickListener {
//
//            when (it.itemId) {
//
//                R.id.create_library -> {
//                    LibraryDialog(requireContext(), viewModel)
//                }
//
////                R.id.sort -> LibrarySortDialog(requireContext(), viewModel)
//
////                R.id.labels -> this.findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLabelListFragment())
//            }
//
//            true
//        }

        binding.fabLibrary.setOnClickListener {
            LibraryDialog(requireContext(), viewModel)
        }

        with(binding.rv) {

            adapter = rvAdapter
            layoutManager = rvLayoutManager

            LibraryItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            viewModel.libraries.observe(viewLifecycleOwner, Observer {
                it?.let {
                    rvAdapter.submitList(it)
                }
            })
        }

//        val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
//        val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)

//        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bnv)
//        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
//        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0) {
//
//                    fab.visibility = View.INVISIBLE
//                    fab.startAnimation(slideDownAnimation)
//
//                    bottomNav.visibility = View.INVISIBLE
//                    bottomNav.startAnimation(slideDownAnimation)
//                } else {
//
//                    fab.visibility = View.VISIBLE
//                    fab.startAnimation(slideUpAnimation)
//
//                    bottomNav.visibility = View.VISIBLE
//                    bottomNav.startAnimation(slideUpAnimation)
//                }
//            }

//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
//                    bottomNav.visibility = View.INVISIBLE
//                    bottomNav.startAnimation(slideDownAnimation)
//                } else {
//                    bottomNav.visibility = View.VISIBLE
//                    bottomNav.startAnimation(slideUpAnimation)
//                }
////                RecyclerView.STATE
//            }
//        })

//
        return binding.root
    }


    override fun onClick(library: Library) {
        this.findNavController()
            .navigate(
                LibraryListFragmentDirections.actionLibraryListFragmentToLibraryFragment(
                    library.libraryId,
                    library.notoColor
                )
            )
    }
}
