package com.noto.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.NotoItemTouchHelper
import com.noto.R
import com.noto.databinding.FragmentLibraryBinding
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.util.getValue
import com.noto.util.setFontFamily
import org.koin.android.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<LibraryFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentLibraryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@LibraryFragment
        }

        binding.ctb.setFontFamily()

        binding.tb.setNavigationOnClickListener { findNavController().navigateUp() }

        viewModel.getLibraryById(args.libraryId)
        viewModel.getNotos(args.libraryId)

        with(binding.fab) {

            imageTintList = ResourcesCompat.getColorStateList(resources, R.color.colorBackground, null)

            setOnClickListener {
                findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId))
            }

        }

        with(binding.bab) {

            navigationIcon?.mutate()?.setTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))

            setNavigationOnClickListener {

            }


            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.archived_notos -> {
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToArchiveFragment(args.libraryId))
                        true
                    }

                    R.id.delete_library -> {
                        findNavController().navigateUp()
                        viewModel.deleteLibrary()
                        true
                    }

                    else -> false
                }
            }

        }

        viewModel.library.observe(viewLifecycleOwner) { library ->

            setLibraryColors(library.notoColor, library.notoIcon)
            binding.ctb.title = library.libraryTitle
            binding.tvLibraryTitle.text = library.libraryTitle

            val notosCount = viewModel.countNotos(library.libraryId)
            binding.tvLibraryNotoCount.text = notosCount.toString().plus(if (notosCount == 1) " Noto" else " Notos")

        }

        with(binding.rv) {

            val rvAdapter = NotoListRVAdapter(viewModel,
                object : NotoItemClickListener {
                    override fun onClick(noto: Noto) {
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(noto.libraryId, noto.notoId))
                    }

                })

            adapter = rvAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            NotoItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            viewModel.notos.observe(viewLifecycleOwner, Observer {
                rvAdapter.submitList(it)
            })
        }

        return binding.root
    }

    private fun setLibraryColors(notoColor: NotoColor, notoIcon: NotoIcon) {

        val color = ResourcesCompat.getColor(resources, notoColor.getValue(), null)

        binding.tvLibraryTitle.setTextColor(color)
        binding.tvLibraryNotoCount.setTextColor(color)
        binding.tb.navigationIcon?.mutate()?.setTint(color)
        binding.ivLibraryIcon.setImageResource(notoIcon.getValue())
        binding.ivLibraryIcon.imageTintList = ResourcesCompat.getColorStateList(resources, notoColor.getValue(), null)
        binding.fab.backgroundTintList = ResourcesCompat.getColorStateList(resources, notoColor.getValue(), null)

    }

    private fun getAllNotos() {

        viewModel.getAllNotos()

    }
}