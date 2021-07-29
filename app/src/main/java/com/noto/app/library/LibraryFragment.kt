package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.app.NotoItemTouchHelper
import com.noto.app.R
import com.noto.app.databinding.LibraryFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LibraryFragment : Fragment() {

    private lateinit var binding: LibraryFragmentBinding

    private val viewModel by viewModel<LibraryViewModel>()

    private val args by navArgs<LibraryFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = LibraryFragmentBinding.inflate(inflater, container, false)

        viewModel.getLibrary(args.libraryId)
        viewModel.getNotes(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner) { library ->

            setLibraryColors(library.color, library.icon)
            binding.tvLibraryTitle.text = library.title
            binding.tvPlaceHolder.text = library.title

            val notosCount = viewModel.notos.value?.size ?: 0
            binding.tvLibraryNotoCount.text = notosCount.toString().plus(if (notosCount == 1) " Noto" else " Notos")

        }

        with(binding) {
            ctb.setFontFamily()
            fab.setOnClickListener { findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId)) }
            tb.setNavigationOnClickListener { findNavController().navigateUp() }
        }

        bab()
        rv()

        return binding.root
    }

    private fun setLibraryColors(notoColor: NotoColor, notoIcon: NotoIcon) {

        val color = colorResource(notoColor.toResource())
        val colorStateList = colorStateResource(notoColor.toResource())

        binding.tvLibraryTitle.setTextColor(color)
        binding.tvLibraryNotoCount.setTextColor(color)
        binding.tb.navigationIcon?.mutate()?.setTint(color)
        binding.ivLibraryIcon.setImageResource(notoIcon.toResource())
        binding.ivLibraryIcon.imageTintList = colorStateList
        binding.fab.backgroundTintList = colorStateList

        binding.tvPlaceHolder.setTextColor(color)
        binding.ivPlaceHolder.setImageResource(notoIcon.toResource())
        binding.ivPlaceHolder.imageTintList = colorStateList
    }

    private fun bab() = with(binding.bab) {
        navigationIcon?.mutate()?.setTint(colorResource(R.color.colorPrimary))

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
                        LayoutManager.Linear -> viewModel.setLayoutManager(LayoutManager.Grid)
                        LayoutManager.Grid -> viewModel.setLayoutManager(LayoutManager.Linear)
                    }

                    true
                }

                R.id.search -> {

                    val searchEt = binding.tilSearch

                    searchEt.isVisible = !searchEt.isVisible

                    val rvAnimation: Animation

                    if (searchEt.isVisible) {

                        rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
                            duration = 250
                        }

                        binding.rv.startAnimation(rvAnimation)
                        searchEt.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                    } else {

                        rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
                            duration = 250
                        }

                        binding.rv.startAnimation(rvAnimation)
                        searchEt.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
                    }

                    true
                }

                else -> false
            }
        }
    }

    private fun rv() = with(binding.rv) {
        val rvAdapter = LibraryAdapter(
            object : NotoItemClickListener {
                override fun onClick(note: Note) = findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoFragment(note.libraryId, note.id))
                override fun onLongClick(note: Note) = findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNotoDialogFragment(note.libraryId, note.id))
                override fun toggleNotoStar(note: Note) {
                    viewModel.toggleNoteStar(note)
                }
            }
        )

        adapter = rvAdapter

        NotoItemTouchHelper(rvAdapter).let {
            ItemTouchHelper(it).attachToRecyclerView(this)
        }

        val layoutManagerMenuItem = binding.bab.menu.findItem(R.id.view)

        viewModel.layoutManager.observe(viewLifecycleOwner) { value ->
            Timber.e("VALUE $value")
            value?.let {

                when (value) {
                    LayoutManager.Linear -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_dashboard_outline)
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    LayoutManager.Grid -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_agenda_outline)
                        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                }

                visibility = View.INVISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }
        }

        val placeHolderItems = listOf(binding.tvLibraryNotoCount, binding.tvLibraryTitle, binding.ivLibraryIcon, binding.rv)

        viewModel.notos.observe(viewLifecycleOwner) { notos ->
            notos?.let {
                if (notos.isEmpty()) {
                    placeHolderItems.forEach { it.visibility = View.GONE }
                    binding.llPlaceHolder.visibility = View.VISIBLE
                    val layoutParams = binding.ctb.layoutParams as AppBarLayout.LayoutParams
                    layoutParams.scrollFlags = 0
                } else {
                    placeHolderItems.forEach { it.visibility = View.VISIBLE }
                    binding.llPlaceHolder.visibility = View.GONE
                    rvAdapter.submitList(notos)
                }
            }
        }

        viewModel.searchTerm.observe(viewLifecycleOwner) { searchTerm ->
            val notos = viewModel.notos.value
            if (searchTerm.isBlank()) rvAdapter.submitList(notos)
            else rvAdapter.submitList(notos?.filter { it.title.contains(searchTerm) || it.body.contains(searchTerm) })
        }
    }

}