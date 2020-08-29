package com.noto.app.library

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.app.NotoItemTouchHelper
import com.noto.R
import com.noto.databinding.FragmentLibraryBinding
import com.noto.domain.model.NotoColor
import com.noto.app.library.LibraryFragmentArgs
import com.noto.app.library.LibraryFragmentDirections
import com.noto.app.util.getValue
import com.noto.app.util.setFontFamily
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.abs

class LibraryFragment : Fragment(), NotoClickListener {

    private val binding by lazy {
        FragmentLibraryBinding.inflate(layoutInflater,null, false).also {
            it.lifecycleOwner = this
        }
    }

    private val rvAdapter by lazy { NotoListRVAdapter(viewModel, args.notoColor, this) }

    private val rvLayoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    private val args by navArgs<LibraryFragmentArgs>()

    private val viewModel by viewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getLibraryById(args.libraryId)

        viewModel.getNotos(args.libraryId)

        setListeners()

        binding.fab.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))

        viewModel.library.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ctb.title = it.libraryTitle
                binding.tvLibraryTitle.text = it.libraryTitle
//                binding.tvLibraryCreationDate.text = formatter.print(it.libraryCreationDate)

                val notosCount = viewModel.countNotos(args.libraryId)
                if (notosCount == 1) {
                    binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Noto")
                } else {
                    binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Notos")
                }

                setNoto(args.notoColor)
            }
        })

        binding.tb.setOnMenuItemClickListener {
            when (it.itemId){
                R.id.delete_library -> {
                    viewModel.deleteLibrary()
                    this.findNavController().navigateUp()
                }
            }
            true
        }

        with(binding.rv) {

            adapter = rvAdapter
            layoutManager = rvLayoutManager

            NotoItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            viewModel.notos.observe(viewLifecycleOwner, Observer {
                this@LibraryFragment.rvAdapter.submitList(it)
//                setList(it, rvAdapter, binding.emptyNotebook)
            })
        }

        binding.abl.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                binding.tb.title = binding.tvLibraryTitle.text
                // Collapsed
            } else if (verticalOffset == 0) {
                binding.tb.title = ""
                // Expanded
            } else {
                binding.tb.title = ""
                // Somewhere in between
            }
        })

        return binding.root
    }

    private fun setNoto(notoColor: NotoColor) {
        with(binding) {
            val color = resources.getColor(notoColor.getValue())
            requireActivity().window?.statusBarColor = color
            tb.setBackgroundColor(color)
            ctb.setContentScrimColor(color)
            ctb.setFontFamily()
            ctb.backgroundTintList = ColorStateList.valueOf(color)
            fab.backgroundTintList = ColorStateList.valueOf(color)
        }
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            this.findNavController().navigate(
//                LibraryFragmentDirections.actionLibraryFragmentToNotoNewFragment()
                LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(
                    args.libraryId,
                    0
                )
            )
//            this.findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId, 0))
        }

        binding.tb.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    override fun onClick(id: Long) {
        this.findNavController().navigate(
//            LibraryFragmentDirections.actionLibraryFragmentToNotoNewFragment()
            LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(
                args.libraryId,
                id
            )
        )
    }
}