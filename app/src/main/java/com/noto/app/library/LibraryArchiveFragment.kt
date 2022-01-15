package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryArchiveFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryArchiveFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryArchiveFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupState(baseDialogFragment)
        }

    private fun LibraryArchiveFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)

    private fun LibraryArchiveFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()

        viewModel.library
            .onEach { library -> setupLibrary(library, baseDialogFragment) }
            .distinctUntilChangedBy { library -> library.layout }
            .onEach { library -> setupLayoutManger(library.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.archivedNotes,
            viewModel.font,
            viewModel.library,
        ) { archivedNotes, font, library ->
            setupArchivedNotes(archivedNotes.map { it.sorted(library.sortingType, library.sortingOrder) }, font, library)
        }.launchIn(lifecycleScope)
    }

    private fun LibraryArchiveFragmentBinding.setupLayoutManger(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    private fun LibraryArchiveFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            baseDialogFragment.tvDialogTitle.text = context.stringResource(R.string.archive, library.getTitle(context))
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            baseDialogFragment.vHead.background?.mutate()?.setTint(color)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun LibraryArchiveFragmentBinding.setupArchivedNotes(state: UiState<List<NoteWithLabels>>, font: Font, library: Library) {
        if (state is UiState.Success) {
            val archivedNotes = state.value
            rv.withModels {
                context?.let { context ->
                    if (archivedNotes.isEmpty())
                        placeholderItem {
                            id("placeholder")
                            placeholder(context.stringResource(R.string.archive_is_empty))
                        }
                    else
                        buildNotesModels(context, library, archivedNotes) { notes ->
                            notes.forEach { archivedNote ->
                                noteItem {
                                    id(archivedNote.first.id)
                                    note(archivedNote.first)
                                    font(font)
                                    previewSize(library.notePreviewSize)
                                    isShowCreationDate(library.isShowNoteCreationDate)
                                    color(library.color)
                                    labels(archivedNote.second)
                                    isManualSorting(false)
                                    onClickListener { _ ->
                                        navController
                                            ?.navigateSafely(
                                                LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteFragment(
                                                    archivedNote.first.libraryId,
                                                    archivedNote.first.id
                                                )
                                            )
                                    }
                                    onLongClickListener { _ ->
                                        navController
                                            ?.navigateSafely(
                                                LibraryArchiveFragmentDirections.actionLibraryArchiveFragmentToNoteDialogFragment(
                                                    archivedNote.first.libraryId,
                                                    archivedNote.first.id,
                                                    R.id.libraryArchiveFragment
                                                )
                                            )
                                        true
                                    }
                                    onDragHandleTouchListener { _, _ -> false }
                                }
                            }
                        }
                }
            }
        }
    }
}