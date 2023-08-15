package com.noto.app.note

import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteSelectionDialogFragmentBinding
import com.noto.app.folder.FolderViewModel
import com.noto.app.folder.noteItem
import com.noto.app.label.labelItem
import com.noto.app.util.Constants
import com.noto.app.util.LineSeparator
import com.noto.app.util.SmoothLinearLayoutManager
import com.noto.app.util.cancelAlarm
import com.noto.app.util.colorResource
import com.noto.app.util.drawableResource
import com.noto.app.util.format
import com.noto.app.util.getTitle
import com.noto.app.util.launchShareNotesIntent
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.quantityStringResource
import com.noto.app.util.snackbar
import com.noto.app.util.stringResource
import com.noto.app.util.toColorResourceId
import com.noto.app.util.updateAllWidgetsData
import com.noto.app.util.updateNoteListWidgets
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteSelectionDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId, args.selectedNoteIds) }

    private val args by navArgs<NoteSelectionDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val parentView by lazy { parentFragment?.view }

    private val anchorViewId by lazy { R.id.bab }

    private val folderColor by lazy { viewModel.folder.value.color }

    private val selectedNotes
        get() = viewModel.selectedNotes.map { it.note }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteSelectionDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    fun NoteSelectionDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.options)
        rv.layoutManager = SmoothLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toColorResourceId())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.notes,
            viewModel.font,
        ) { folder, notesState, font ->
            if (notesState is UiState.Success) {
                val notes = notesState.value.filter { it.isSelected }.map { it.copy(isSelected = false) }
                rv.withModels {
                    notes.forEach { model ->
                        noteItem {
                            id(model.note.id)
                            model(model)
                            isPreview(true)
                            parentWidth(rv.width)
                            font(font)
                            color(folder.color)
                            previewSize(folder.notePreviewSize)
                            isShowCreationDate(folder.isShowNoteCreationDate)
                            searchTerm("")
                            isManualSorting(false)
                            isSelection(false)
                        }
                    }
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.notes
            .onEach { state ->
                if (state is UiState.Success) {
                    val selectedNotes = state.value.filter { it.isSelected }
                    val selectedNotesCount = selectedNotes.count()
                    if (selectedNotes.none { it.note.isPinned }) {
                        tvPinNotes.text = context?.stringResource(R.string.pin)
                        tvPinNotes.compoundDrawablesRelative[1] = context?.drawableResource(R.drawable.ic_round_pin_24)
                        tvPinNotes.setOnClickListener {
                            disableSelection()
                            viewModel.pinSelectedNotes().invokeOnCompletion {
                                context?.let { context ->
                                    val text = context.quantityStringResource(R.plurals.note_is_pinned, selectedNotesCount, selectedNotesCount)
                                    val drawableId = R.drawable.ic_round_pin_24
                                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                                    context.updateAllWidgetsData()
                                }
                                dismiss()
                            }
                        }
                    } else {
                        tvPinNotes.text = context?.stringResource(R.string.unpin)
                        tvPinNotes.compoundDrawablesRelative[1] = context?.drawableResource(R.drawable.ic_round_pin_off_24)
                        tvPinNotes.setOnClickListener {
                            disableSelection()
                            viewModel.unpinSelectedNotes().invokeOnCompletion {
                                context?.let { context ->
                                    val text =
                                        context.quantityStringResource(R.plurals.note_is_unpinned, selectedNotesCount, selectedNotesCount)
                                    val drawableId = R.drawable.ic_round_pin_off_24
                                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                                    context.updateAllWidgetsData()
                                }
                                dismiss()
                            }
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

        combine(viewModel.folder, viewModel.selectionLabels) { folder, labels ->
            rvLabels.withModels {
                labels.forEach { model ->
                    labelItem {
                        id(model.label.id)
                        label(model.label)
                        isSelected(model.isSelected)
                        color(folder.color)
                        onClickListener { _ ->
                            if (model.isSelected)
                                viewModel.deselectLabelForSelectedNotes(model.label.id)
                            else
                                viewModel.selectLabelForSelectedNotes(model.label.id)
                        }
                        onLongClickListener { _ -> false }
                    }
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.previewNotePosition
            .filter { it != -1 }
            .onEach { position -> rv.smoothScrollToPosition(position) }
            .launchIn(lifecycleScope)

        rv.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val currentView = snapHelper.findSnapView(rv.layoutManager)
                    if (currentView != null) {
                        val position = rv.getChildAdapterPosition(currentView)
                        viewModel.setCurrentNotePosition(position)
                    }
                    viewModel.setIsUserScrolling(dx > 0 || dx < 0)
                }
            }
        )
    }

    fun NoteSelectionDialogFragmentBinding.setupListeners() {
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        tvReadingMode.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            navController?.navigateSafely(
                NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToNotePagerFragment(
                    folderId = args.folderId,
                    noteId = selectedNotes.first().id,
                    selectedNoteIds = selectedNotes.map { it.id }.toLongArray()
                )
            )
            dismiss()
        }

        tvMergeNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            viewModel.mergeSelectedNotes().invokeOnCompletion {
                context?.let { context ->
                    val text = context.stringResource(R.string.notes_are_merged, selectedNotes.count())
                    val drawableId = R.drawable.ic_round_merge_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                    context.updateNoteListWidgets()
                }
                dismiss()
            }
        }

        tvShareNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            launchShareNotesIntent(selectedNotes)
            dismiss()
        }

        tvArchiveNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            viewModel.archiveSelectedNotes().invokeOnCompletion {
                context?.let { context ->
                    val text = context.quantityStringResource(R.plurals.note_is_archived, selectedNotes.count(), selectedNotes.count())
                    val drawableId = R.drawable.ic_round_archive_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                    selectedNotes.filter { note -> note.reminderDate != null }
                        .forEach { note -> alarmManager?.cancelAlarm(context, note.id) }
                    context.updateAllWidgetsData()
                    context.updateNoteListWidgets()
                }
                dismiss()
            }
        }

        tvDuplicateNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            viewModel.duplicateSelectedNotes().invokeOnCompletion {
                context?.let { context ->
                    val text = context.quantityStringResource(R.plurals.note_is_duplicated, selectedNotes.count(), selectedNotes.count())
                    val drawableId = R.drawable.ic_round_control_point_duplicate_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                }
                dismiss()
            }
        }

        tvCopyToClipboard.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            context?.let { context ->
                val notesText = selectedNotes.joinToString(LineSeparator) { it.format() }
                val clipData = ClipData.newPlainText(viewModel.folder.value.getTitle(context), notesText)
                clipboardManager?.setPrimaryClip(clipData)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    val text = context.quantityStringResource(R.plurals.note_copied_to_clipboard, selectedNotes.count(), selectedNotes.count())
                    val drawableId = R.drawable.ic_round_copy_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                }
            }
            dismiss()
        }

        tvCopyNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            savedStateHandle?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    context?.let { context ->
                        val folderTitle = savedStateHandle.get<String>(Constants.FolderTitle)
                        navController?.navigateUp()
                        navController?.navigateSafely(
                            NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToProgressIndicatorDialogFragment(
                                title = context.stringResource(R.string.copying_notes, folderTitle)
                            )
                        )
                        viewModel.copySelectedNotes(folderId).invokeOnCompletion {
                            val text = context.quantityStringResource(
                                R.plurals.note_is_copied,
                                selectedNotes.count(),
                                selectedNotes.count(),
                                folderTitle
                            )
                            val drawableId = R.drawable.ic_round_file_copy_24
                            parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            dismiss()
                        }
                    }
                }
            context?.let { context ->
                navController?.navigateSafely(
                    NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToSelectFolderDialogFragment(
                        filteredFolderIds = longArrayOf(args.folderId),
                        title = context.stringResource(R.string.copy_to).removeSuffix("…")
                    )
                )
            }
        }

        tvMoveNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            savedStateHandle?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    context?.let { context ->
                        val folderTitle = savedStateHandle.get<String>(Constants.FolderTitle)
                        navController?.navigateUp()
                        navController?.navigateSafely(
                            NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToProgressIndicatorDialogFragment(
                                title = context.stringResource(R.string.moving_notes, folderTitle)
                            )
                        )
                        viewModel.moveSelectedNotes(folderId).invokeOnCompletion {
                            val text = context.quantityStringResource(
                                R.plurals.note_is_moved,
                                selectedNotes.count(),
                                selectedNotes.count(),
                                folderTitle,
                            )
                            val drawableId = R.drawable.ic_round_move_24
                            parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            dismiss()
                        }
                    }
                }
            context?.let { context ->
                navController?.navigateSafely(
                    NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToSelectFolderDialogFragment(
                        filteredFolderIds = longArrayOf(args.folderId),
                        title = context.stringResource(R.string.move_to).removeSuffix("…")
                    )
                )
            }
        }

        tvDeleteNotes.setOnClickListener {
            val selectedNotes = selectedNotes
            disableSelection()
            context?.let { context ->
                val confirmationText = context.quantityStringResource(R.plurals.delete_note_confirmation, selectedNotes.count())
                val descriptionText = context.quantityStringResource(R.plurals.delete_note_description, selectedNotes.count())
                val btnText = context.quantityStringResource(R.plurals.delete_note, selectedNotes.count())
                savedStateHandle?.getLiveData<Int>(Constants.ClickListener)
                    ?.observe(viewLifecycleOwner) {
                        viewModel.deleteSelectedNotes().invokeOnCompletion {
                            val text = context.quantityStringResource(R.plurals.note_is_deleted, selectedNotes.count(), selectedNotes.count())
                            val drawableId = R.drawable.ic_round_delete_24
                            parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                            selectedNotes.filter { note -> note.reminderDate != null }
                                .forEach { note -> alarmManager?.cancelAlarm(context, note.id) }
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            dismiss()
                        }
                    }

                navController?.navigateSafely(
                    NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
        }
    }

    private fun disableSelection() {
        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.DisableSelection, true)
    }
}