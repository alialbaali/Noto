package com.noto.app.notelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.noto.app.R
import com.noto.app.databinding.NoteListFragmentBinding
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListFragment : Fragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListFragmentArgs>()

    private val adapter by lazy { NoteListAdapter(noteItemClickListener) }

    private val noteItemClickListener by lazy {
        object : NoteListAdapter.NoteItemClickListener {
            override fun onClick(note: Note) = findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoFragment(note.libraryId, note.id))
            override fun onLongClick(note: Note) = findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoDialogFragment(note.libraryId, note.id))
            override fun toggleNotoStar(note: Note) {
                viewModel.toggleNoteStar(note)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteListFragmentBinding.inflate(inflater, container, false).withBinding {
            collectState()
            setupListeners()
            setupRV()
            setupUI()
        }

    private fun NoteListFragmentBinding.setupUI() {
        ctb.setFontFamily()
    }

    private fun NoteListFragmentBinding.collectState() {
        viewModel.library
            .onEach {
                setLibraryColors(it.color, it.icon)
                tvLibraryTitle.text = it.title
                tvPlaceHolder.text = it.title
                val notosCount = viewModel.notes.value.size
                tvLibraryNotoCount.text = notosCount.toString().plus(if (notosCount == 1) " Note" else " Notes")
            }
            .launchIn(lifecycleScope)

        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout_manager)

        viewModel.layoutManager
            .onEach {
                when (it) {
                    LayoutManager.Linear -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_dashboard_outline)
                        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    }
                    LayoutManager.Grid -> {
                        layoutManagerMenuItem.icon = drawableResource(R.drawable.view_agenda_outline)
                        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    }
                }

                rv.visibility = View.INVISIBLE
                rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

                rv.visibility = View.VISIBLE
                rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))

            }
            .launchIn(lifecycleScope)

        val placeHolderItems = listOf(tvLibraryNotoCount, tvLibraryTitle, ivLibraryIcon, rv)

        viewModel.notes
            .onEach {
                if (it.isEmpty()) {
                    placeHolderItems.forEach { it.visibility = View.GONE }
                    llPlaceHolder.visibility = View.VISIBLE
                    val layoutParams = ctb.layoutParams as AppBarLayout.LayoutParams
                    layoutParams.scrollFlags = 0
                } else {
                    placeHolderItems.forEach { it.visibility = View.VISIBLE }
                    llPlaceHolder.visibility = View.GONE
                    adapter.submitList(it)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteListFragmentBinding.setLibraryColors(notoColor: NotoColor, notoIcon: NotoIcon) {

        val color = colorResource(notoColor.toResource())
        val colorStateList = colorStateResource(notoColor.toResource())

        tvLibraryTitle.setTextColor(color)
        tvLibraryNotoCount.setTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        ivLibraryIcon.setImageResource(notoIcon.toResource())
        ivLibraryIcon.imageTintList = colorStateList
        fab.backgroundTintList = colorStateList

        tvPlaceHolder.setTextColor(color)
        ivPlaceHolder.setImageResource(notoIcon.toResource())
        ivPlaceHolder.imageTintList = colorStateList
    }

    private fun NoteListFragmentBinding.setupListeners() {

        fab.setOnClickListener {
            findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToNotoFragment(args.libraryId))
        }
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archived_notos -> {
                    findNavController().navigate(NoteListFragmentDirections.actionLibraryFragmentToArchiveFragment(args.libraryId))
                    true
                }

                R.id.layout_manager -> {
                    when (viewModel.layoutManager.value) {
                        LayoutManager.Linear -> viewModel.updateLayoutManager(LayoutManager.Grid)
                        LayoutManager.Grid -> viewModel.updateLayoutManager(LayoutManager.Linear)
                    }
                    true
                }

                R.id.search -> {
                    val searchEt = tilSearch
                    searchEt.isVisible = !searchEt.isVisible
                    val rvAnimation: Animation
                    if (searchEt.isVisible) {
                        rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
                            duration = 250
                        }
                        rv.startAnimation(rvAnimation)
                        searchEt.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))
                    } else {
                        rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
                            duration = 250
                        }
                        rv.startAnimation(rvAnimation)
                        searchEt.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun NoteListFragmentBinding.setupRV() {
        rv.adapter = adapter
    }

}
