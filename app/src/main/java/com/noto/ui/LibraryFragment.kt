package com.noto.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.adapter.NavigateToNote
import com.noto.adapter.NotoListRVAdapter
import com.noto.databinding.FragmentLibraryBinding
import com.noto.domain.NotoColor
import com.noto.domain.NotoIcon
import com.noto.util.setFontFamily
import com.noto.viewModel.LibraryViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DateFormat
import kotlin.math.abs

class LibraryFragment : Fragment(), NavigateToNote {

    private val binding by lazy {
        FragmentLibraryBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
        }
    }

    private val rvAdapter by lazy { NotoListRVAdapter(args.notoColor, this) }

    private val rvLayoutManager by lazy { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }

    private val args by navArgs<LibraryFragmentArgs>()

    private val viewModel by viewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getLibrary(args.libraryId)

        viewModel.getNotos(args.libraryId)

        setListeners()

        val sdf = DateFormat.getDateInstance(DateFormat.MEDIUM)
        viewModel.library.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.ctb.title = it.libraryTitle
                binding.tvLibraryTitle.text = it.libraryTitle
                val notosCount = viewModel.countNotos(args.libraryId)
                if (notosCount == 1) {
                    binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Noto")
                } else {
                    binding.tvLibraryNotoCount.text = notosCount.toString().plus(" Notos")
                }
                binding.tvLibraryCreationDate.text = sdf.format(it.libraryCreationDate).toString()
                setNoto(args.notoColor, it.notoIcon)
            }
        })

        with(binding.rv) {

            adapter = rvAdapter
            layoutManager = rvLayoutManager

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

    private fun setNoto(notoColor: NotoColor, notoIcon: NotoIcon) {
        with(binding) {
            val color = resources.getColor(notoColor.resId, null)
            requireActivity().window?.statusBarColor = color
            tb.setBackgroundColor(color)
            ctb.setContentScrimColor(color)
            ctb.setFontFamily()
            ctb.backgroundTintList = ColorStateList.valueOf(color)
            fab.backgroundTintList = ColorStateList.valueOf(color)
            this.ivNotoIcon.setImageResource(notoIcon.resId)
        }
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            this.findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId, 0))
//            this.findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId, 0))
        }

        binding.tb.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    override fun navigate(id: Long) {
        this.findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId, id))
    }
}