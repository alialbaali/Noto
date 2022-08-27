package com.noto.app.folder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.FolderArchiveFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Layout
import com.noto.app.getOrDefault
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderArchiveFragment : Fragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<FolderArchiveFragmentArgs>()

    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FolderArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    private fun FolderArchiveFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL).also(rv::setLayoutManager)

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .distinctUntilChangedBy { folder -> folder.layout }
            .onEach { folder -> setupLayoutManger(folder.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.archivedNotes,
            viewModel.font,
            viewModel.folder,
        ) { archivedNotes, font, folder ->
            val notesCount = archivedNotes.getOrDefault(emptyList()).count()
            tvFolderNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            tvFolderNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            setupArchivedNotes(archivedNotes.map { it.sorted(folder.sortingType, folder.sortingOrder) }, font, folder)
        }.launchIn(lifecycleScope)

        if (isCurrentLocaleArabic()) {
            tvFolderNotesCount.isVisible = false
            tvFolderNotesCountRtl.isVisible = true
        } else {
            tvFolderNotesCount.isVisible = true
            tvFolderNotesCountRtl.isVisible = false
        }
    }

    private fun FolderArchiveFragmentBinding.setupListeners() {
        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }
    }

    private fun setupLayoutManger(layout: Layout) {
        when (layout) {
            Layout.Linear -> layoutManager.spanCount = 1
            Layout.Grid -> layoutManager.spanCount = 2
        }
    }

    private fun FolderArchiveFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            tvFolderTitle.text = context.stringResource(R.string.folder_archive, folder.getTitle(context))
            tvFolderTitle.setTextColor(color)
            tb.setNavigationIconTint(color)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FolderArchiveFragmentBinding.setupArchivedNotes(state: UiState<List<NoteItemModel>>, font: Font, folder: Folder) {
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
                        buildNotesModels(
                            context,
                            folder,
                            archivedNotes,
                            onCreateClick = {
                                navController?.navigateSafely(
                                    FolderArchiveFragmentDirections.actionFolderArchiveFragmentToNoteFragment(
                                        args.folderId,
                                        labelsIds = it.toLongArray()
                                    )
                                )
                            }
                        ) { notes ->
                            notes.forEach { archivedNoteModel ->
                                noteItem {
                                    id(archivedNoteModel.note.id)
                                    model(archivedNoteModel)
                                    font(font)
                                    searchTerm("")
                                    previewSize(folder.notePreviewSize)
                                    isShowCreationDate(folder.isShowNoteCreationDate)
                                    color(folder.color)
                                    isManualSorting(false)
                                    onClickListener { _ ->
                                        navController
                                            ?.navigateSafely(
                                                FolderArchiveFragmentDirections.actionFolderArchiveFragmentToNoteFragment(
                                                    archivedNoteModel.note.folderId,
                                                    archivedNoteModel.note.id
                                                )
                                            )
                                    }
                                    onLongClickListener { _ ->
                                        navController
                                            ?.navigateSafely(
                                                FolderArchiveFragmentDirections.actionFolderArchiveFragmentToNoteDialogFragment(
                                                    archivedNoteModel.note.folderId,
                                                    archivedNoteModel.note.id,
                                                    R.id.folderArchiveFragment
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