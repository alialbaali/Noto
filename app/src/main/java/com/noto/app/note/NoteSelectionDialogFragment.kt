package com.noto.app.note

import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.NoteSelectionDialogFragmentBinding
import com.noto.app.folder.FolderViewModel
import com.noto.app.folder.noteItem
import com.noto.app.getOrDefault
import com.noto.app.label.labelItem
import com.noto.app.util.*
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

    private val anchorViewId by lazy { R.id.fab }

    private val folderColor by lazy { viewModel.folder.value.color }

    private val selectedNotes
        get() = viewModel.notes.value.getOrDefault(emptyList()).map { it.note }

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

        combine(
            viewModel.folder,
            viewModel.notes,
            viewModel.font,
        ) { folder, notesState, font ->
            if (notesState is UiState.Success) {
                val notes = notesState.value.map { it.copy(isSelected = false) }
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
            .onEach {
                if (it is UiState.Success) {
                    val isAllSelected = it.value.all { it.isSelected }
                    tvSelectAllNotes.isVisible = !isAllSelected
                    divider2.root.isVisible = !isAllSelected
                    val selectedNotesCount = it.value.count { it.isSelected }
                    if (it.value.none { it.note.isPinned }) {
                        tvPinNotes.text = context?.stringResource(R.string.pin)
                        tvPinNotes.setOnClickListener {
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
                        tvPinNotes.setOnClickListener {
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
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(folder.color)
                        onClickListener { _ ->
                            if (entry.value)
                                viewModel.deselectLabelForSelectedNotes(entry.key.id)
                            else
                                viewModel.selectLabelForSelectedNotes(entry.key.id)
                        }
                        onLongClickListener { _ ->
                            if (entry.value)
                                viewModel.deselectLabelForSelectedNotes(entry.key.id)
                            else
                                viewModel.selectLabelForSelectedNotes(entry.key.id)
                            true
                        }
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
        tvSelectAllNotes.setOnClickListener {
            navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.SelectAll, true)
            dismiss()
        }

        tvMergeNotes.setOnClickListener {
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
            val notesText = selectedNotes.joinToString(LineSeparator) { it.format() }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, notesText)
            }
            val chooser = Intent.createChooser(intent, context?.stringResource(R.string.share_note))
            startActivity(chooser)
            dismiss()
        }

        tvArchiveNotes.setOnClickListener {
            viewModel.archiveSelectedNotes().invokeOnCompletion {
                context?.let { context ->
                    val text = context.quantityStringResource(R.plurals.note_is_archived, selectedNotes.count(), selectedNotes.count())
                    val drawableId = R.drawable.ic_round_archive_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                    context.updateNoteListWidgets()
                }
                dismiss()
            }
        }

        tvDuplicateNotes.setOnClickListener {
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
            navController?.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.copySelectedNotes(folderId).invokeOnCompletion {
                        context?.let { context ->
                            val folderTitle = navController?.currentBackStackEntry?.savedStateHandle?.get<String>(Constants.FolderTitle)
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
                        }
                        dismiss()
                    }
                }
            navController?.navigateSafely(
                NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToSelectFolderDialogFragment(
                    longArrayOf(
                        args.folderId
                    )
                )
            )
        }

        tvMoveNotes.setOnClickListener {
            navController?.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.moveSelectedNotes(folderId).invokeOnCompletion {
                        context?.let { context ->
                            val folderTitle = navController?.currentBackStackEntry?.savedStateHandle?.get<String>(Constants.FolderTitle)
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
                        }
                        dismiss()
                    }
                }
            navController?.navigateSafely(
                NoteSelectionDialogFragmentDirections.actionNoteSelectionDialogFragmentToSelectFolderDialogFragment(
                    longArrayOf(
                        args.folderId
                    )
                )
            )
        }

        tvDeleteNotes.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.quantityStringResource(R.plurals.delete_note_confirmation, selectedNotes.count())
                val descriptionText = context.quantityStringResource(R.plurals.delete_note_description, selectedNotes.count())
                val btnText = context.quantityStringResource(R.plurals.delete_note, selectedNotes.count())
                navController?.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Int>(Constants.ClickListener)
                    ?.observe(viewLifecycleOwner) {
                        viewModel.deleteSelectedNotes().invokeOnCompletion {
                            val text = context.quantityStringResource(R.plurals.note_is_deleted, selectedNotes.count(), selectedNotes.count())
                            val drawableId = R.drawable.ic_round_delete_24
                            parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                            selectedNotes.forEach { note ->
                                if (note.reminderDate != null)
                                    alarmManager?.cancelAlarm(context, note.id)
                            }
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
}