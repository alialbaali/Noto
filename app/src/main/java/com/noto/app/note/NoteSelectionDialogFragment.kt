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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.NoteSelectionDialogFragmentBinding
import com.noto.app.folder.FolderViewModel
import com.noto.app.getOrDefault
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val LineSeparator = "\n\n"

class NoteSelectionDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId, args.selectedNoteIds) }

    private val args by navArgs<NoteSelectionDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val parentView by lazy { parentFragment?.view }

    private val anchorViewId by lazy { R.id.fab }

    private val folderColor by lazy { viewModel.folder.value.color }

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
        viewModel.notes
            .onEach {
                if (it is UiState.Success) {
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
    }

    fun NoteSelectionDialogFragmentBinding.setupListeners() {
        tvShareNotes.setOnClickListener {
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            viewModel.archiveSelectedNotes()
        }

        tvDuplicateNotes.setOnClickListener {
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
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
            val selectedNotes = viewModel.notes.value.getOrDefault(emptyList()).filter { it.isSelected }.map { it.note }
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_note_confirmation)
                val descriptionText = context.stringResource(R.string.delete_note_description)
                val btnText = context.stringResource(R.string.delete_note)
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