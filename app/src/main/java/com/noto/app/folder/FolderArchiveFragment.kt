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
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.FolderArchiveFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Layout
import com.noto.app.getOrDefault
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

    private val anchorViewId by lazy { R.id.bab }

    private val folderColor by lazy { viewModel.folder.value.color }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FolderArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    private fun FolderArchiveFragmentBinding.setupState() {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
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
            viewModel.isSelection,
        ) { archivedNotes, font, folder, isSelection ->
            val notesCount = archivedNotes.getOrDefault(emptyList()).count()
            val selectedNotesCount = archivedNotes.getOrDefault(emptyList()).count { it.isSelected }
            if (selectedNotesCount != 0) {
                tvFolderNotesCount.text = context?.quantityStringResource(R.plurals.notes_selected_count, notesCount, notesCount, selectedNotesCount)
                tvFolderNotesCountRtl.text =
                    context?.quantityStringResource(R.plurals.notes_selected_count, notesCount, notesCount, selectedNotesCount)
                fabUnarchive.enable()
                fabDelete.enable()
            } else {
                tvFolderNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvFolderNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                fabUnarchive.disable()
                fabDelete.disable()
            }

            setupArchivedNotes(archivedNotes, font, folder, isSelection)
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
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        activity?.onBackPressedDispatcher?.addCallback {
            val selectedNotes = viewModel.selectedArchivedNotes
            if (selectedNotes.isEmpty()) navController?.navigateUp() else viewModel.deselectAllArchivedNotes()
        }

        fabUnarchive.setOnClickListener {
            val selectedNotes = viewModel.selectedArchivedNotes
            context?.let { context ->
                viewModel.unarchiveSelectedArchivedNotes()
                val text = context.quantityStringResource(R.plurals.note_is_unarchived, selectedNotes.count(), selectedNotes.count())
                val drawableId = R.drawable.ic_round_unarchive_24
                root.snackbar(text, drawableId, anchorViewId, folderColor)
                context.updateAllWidgetsData()
                context.updateNoteListWidgets()
            }
        }

        fabDelete.setOnClickListener {
            val selectedNotes = viewModel.selectedArchivedNotes
            context?.let { context ->
                val confirmationText = context.quantityStringResource(R.plurals.delete_note_confirmation, selectedNotes.count())
                val descriptionText = context.quantityStringResource(R.plurals.delete_note_description, selectedNotes.count())
                val btnText = context.quantityStringResource(R.plurals.delete_note, selectedNotes.count())
                val liveData = savedStateHandle?.getLiveData<Int>(Constants.ClickListener)
                liveData?.observe(viewLifecycleOwner) {
                    if (it != null) {
                        liveData.value = null
                        viewModel.deleteSelectedArchivedNotes().invokeOnCompletion {
                            val text = context.quantityStringResource(R.plurals.note_is_deleted, selectedNotes.count(), selectedNotes.count())
                            val drawableId = R.drawable.ic_round_delete_24
                            root.snackbar(text, drawableId, anchorViewId, folderColor)
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                        }
                    }
                }

                navController?.navigateSafely(
                    FolderArchiveFragmentDirections.actionFolderArchiveFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
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
            val color = context.colorResource(folder.color.toColorResourceId())
            tvFolderTitle.text = context.stringResource(R.string.folder_archive, folder.getTitle(context))
            tvFolderTitle.setTextColor(color)
            tb.setNavigationIconTint(color)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FolderArchiveFragmentBinding.setupArchivedNotes(
        state: UiState<List<NoteItemModel>>,
        font: Font,
        folder: Folder,
        isSelection: Boolean,
    ) {
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
                                        labelsIds = it.toLongArray(),
                                        selectedNoteIds = longArrayOf(),
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
                                    isSelection(isSelection)
                                    isShowCreationDate(folder.isShowNoteCreationDate)
                                    color(folder.color)
                                    isManualSorting(false)
                                    onClickListener { _ ->
                                        if (isSelection) {
                                            if (archivedNoteModel.isSelected)
                                                viewModel.deselectArchivedNote(archivedNoteModel.note.id)
                                            else
                                                viewModel.selectArchivedNote(archivedNoteModel.note.id)
                                        } else {
                                            navController?.navigateSafely(
                                                FolderArchiveFragmentDirections.actionFolderArchiveFragmentToNotePagerFragment(
                                                    args.folderId,
                                                    archivedNoteModel.note.id,
                                                    archivedNotes.map { it.note.id }.toLongArray(),
                                                    isArchive = true,
                                                )
                                            )
                                        }
                                    }
                                    onLongClickListener { _ ->
                                        viewModel.enableSelection()
                                        if (archivedNoteModel.isSelected)
                                            viewModel.deselectArchivedNote(archivedNoteModel.note.id)
                                        else
                                            viewModel.selectArchivedNote(archivedNoteModel.note.id)
                                        true
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}