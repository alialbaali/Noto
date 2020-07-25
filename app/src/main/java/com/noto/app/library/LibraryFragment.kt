package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.app.NotoItemTouchHelper
import com.noto.app.R
import com.noto.app.databinding.FragmentLibraryBinding
import com.noto.app.util.getValue
import com.noto.app.util.setFontFamily
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<LibraryFragmentArgs>()

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentLibraryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@LibraryFragment
        }

        binding.ctb.setFontFamily()

        binding.tb.setNavigationOnClickListener { findNavController().navigateUp() }

        viewModel.getLibrary(args.libraryId)
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
                findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
            }


            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.archived_notos -> {
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToArchiveFragment(args.libraryId))
                        true
                    }

                    R.id.view -> {

                        when (viewModel.layoutManager.value) {
                            LINEAR_LAYOUT_MANAGER -> viewModel.setLayoutManager(STAGGERED_LAYOUT_MANAGER)
                            STAGGERED_LAYOUT_MANAGER -> viewModel.setLayoutManager(LINEAR_LAYOUT_MANAGER)
                        }

                        true
                    }

                    else -> false
                }
            }

        }

        viewModel.library.observe(viewLifecycleOwner) { library ->

            setLibraryColors(library.notoColor, library.notoIcon)
            binding.tvLibraryTitle.text = library.libraryTitle
            binding.tvPlaceHolder.text = library.libraryTitle

            val notosCount = viewModel.notos.value?.size ?: 0
            binding.tvLibraryNotoCount.text = notosCount.toString().plus(if (notosCount == 1) " Noto" else " Notos")

        }

        with(binding.rv) {

            val rvAdapter = NotoListRVAdapter(
                object : NotoItemClickListener {

                    override fun onClick(noto: Noto) {
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(noto.libraryId, noto.notoId))
                    }

                    override fun onLongClick(noto: Noto) {
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoDialogFragment(noto.libraryId, noto.notoId))
                    }

                })

            adapter = rvAdapter

            NotoItemTouchHelper(rvAdapter).let {
                ItemTouchHelper(it).attachToRecyclerView(this)
            }

            val layoutManagerMenuItem = binding.bab.menu.findItem(R.id.view)

            viewModel.layoutManager.observe(viewLifecycleOwner) { value ->

                when (value) {
                    LINEAR_LAYOUT_MANAGER -> {
                        layoutManagerMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.view_dashboard_outline, null)
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    STAGGERED_LAYOUT_MANAGER -> {
                        layoutManagerMenuItem.icon = ResourcesCompat.getDrawable(resources, R.drawable.view_agenda_outline, null)
                        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                }

                visibility = View.INVISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }

            val placeHolderItems = listOf(binding.tvLibraryNotoCount, binding.tvLibraryTitle, binding.ivLibraryIcon, binding.rv)

            viewModel.notos.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    placeHolderItems.forEach { it.visibility = View.GONE }
                    binding.llPlaceHolder.visibility = View.VISIBLE
                    val layoutParams = binding.ctb.layoutParams as AppBarLayout.LayoutParams
                    layoutParams.scrollFlags = 0
                } else {
                    placeHolderItems.forEach { it.visibility = View.VISIBLE }
                    binding.llPlaceHolder.visibility = View.GONE
                    rvAdapter.submitList(it)
                }
            }
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

        binding.tvPlaceHolder.setTextColor(color)
        binding.ivPlaceHolder.setImageResource(notoIcon.getValue())
        binding.ivPlaceHolder.imageTintList = ResourcesCompat.getColorStateList(resources, notoColor.getValue(), null)
    }

}