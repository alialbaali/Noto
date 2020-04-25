package com.noto.ui

import android.app.NotificationManager
import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.R
import com.noto.adapter.LibraryItemClickListener
import com.noto.adapter.LibraryItemTouchHelper
import com.noto.adapter.LibraryListRVAdapter
import com.noto.databinding.FragmentListLibraryBinding
import com.noto.domain.Library
import com.noto.util.createNotification
import com.noto.util.createNotificationChannel
import com.noto.util.setFontFamily
import com.noto.viewModel.LibraryListViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LibraryListFragment : Fragment(), LibraryItemClickListener {

    // Binding
    private val binding by lazy {
        FragmentListLibraryBinding.inflate(layoutInflater).also {
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
        requireActivity().window?.statusBarColor = resources.getColor(R.color.colorPrimary, null)

        viewModel.getLibraries()

        binding.ctb.setFontFamily()

        binding.tb.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.create_library -> NotoDialog(requireContext(), viewModel)

                R.id.sort -> SortDialog(requireContext(), viewModel)

                R.id.create_label -> this.findNavController().navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLabelListFragment())
            }

            true
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

        val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bnv)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {

                    fab.visibility = View.INVISIBLE
                    fab.startAnimation(slideDownAnimation)

                    bottomNav.visibility = View.INVISIBLE
                    bottomNav.startAnimation(slideDownAnimation)
                } else {

                    fab.visibility = View.VISIBLE
                    fab.startAnimation(slideUpAnimation)

                    bottomNav.visibility = View.VISIBLE
                    bottomNav.startAnimation(slideUpAnimation)
                }
            }

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
        })


        return binding.root
    }


    override fun onClick(library: Library) {
        this.findNavController()
            .navigate(LibraryListFragmentDirections.actionLibraryListFragmentToLibraryFragment(library.libraryId, library.notoColor))
    }
}
