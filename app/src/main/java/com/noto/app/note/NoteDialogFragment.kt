package com.noto.app.note

import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.library.SelectLibraryDialogFragment
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialog = setupBaseDialogFragment()
        setupListeners()
        setupState(baseDialog)
    }

    private fun NoteDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.note_options)
        }
    }

    private fun NoteDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.library
            .onEach { library -> setupLibrary(library, baseDialogFragment) }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach { note -> setupNote(note) }
            .launchIn(lifecycleScope)
    }

    private fun NoteDialogFragmentBinding.setupListeners() {
        val parentView = parentFragment?.view
        val parentAnchorView = parentView?.findViewById<FloatingActionButton>(R.id.fab)

        tvArchiveNote.setOnClickListener {
            viewModel.toggleNoteIsArchived().invokeOnCompletion {
                val resource = if (viewModel.note.value.isArchived)
                    R.string.note_is_unarchived
                else
                    R.string.note_is_archived

                context?.let { context ->
                    parentView?.snackbar(context.stringResource(resource), parentAnchorView)
                }

                dismiss()
            }
        }

        tvRemindMe.setOnClickListener {
            dismiss()
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReminderDialogFragment(
                    args.libraryId,
                    args.noteId
                )
            )
        }

        tvOpenInReadingMode.setOnClickListener {
            dismiss()
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReadingModeFragment(
                    args.libraryId,
                    args.noteId
                )
            )
        }

        tvDuplicateNote.setOnClickListener {
            viewModel.duplicateNote().invokeOnCompletion {
                context?.let { context ->
                    parentView?.snackbar(context.stringResource(R.string.note_is_duplicated), parentAnchorView)
                }
                dismiss()
            }
        }

        tvPinNote.setOnClickListener {
            viewModel.toggleNoteIsPinned().invokeOnCompletion {
                val resource = if (viewModel.note.value.isPinned)
                    R.string.note_is_unpinned
                else
                    R.string.note_is_pinned

                context?.let { context ->
                    parentView?.snackbar(context.stringResource(resource), parentAnchorView)
                }

                dismiss()
            }
        }

        tvCopyToClipboard.setOnClickListener {
            val clipData = ClipData.newPlainText(viewModel.library.value.title, viewModel.note.value.format())
            clipboardManager?.setPrimaryClip(clipData)
            context?.let { context ->
                parentView?.snackbar(context.stringResource(R.string.note_copied_to_clipboard), anchorView = parentAnchorView)
            }
            dismiss()
        }

        tvCopyNote.setOnClickListener {
            val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener {
                viewModel.copyNote(it).invokeOnCompletion {
                    context?.let { context ->
                        parentView?.snackbar(context.stringResource(R.string.note_is_copied), anchorView = parentAnchorView)
                    }
                    navController?.popBackStack(args.destination, false)
                    dismiss()
                }
            }
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectLibraryDialogFragment(
                    selectLibraryItemClickListener,
                    args.libraryId
                )
            )
        }

        tvMoveNote.setOnClickListener {
            val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener {
                viewModel.moveNote(it).invokeOnCompletion {
                    context?.let { context ->
                        parentView?.snackbar(context.stringResource(R.string.note_is_moved), anchorView = parentAnchorView)
                    }
                    navController?.popBackStack(args.destination, false)
                    dismiss()
                }
            }
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectLibraryDialogFragment(
                    selectLibraryItemClickListener,
                    args.libraryId
                )
            )
        }

        tvShareNote.setOnClickListener {
            dismiss()
            launchShareNoteIntent(viewModel.note.value)
        }

        tvDeleteNote.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_note_confirmation)
                val descriptionText = context.stringResource(R.string.delete_note_description)
                val btnText = context.stringResource(R.string.delete_note)
                val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                    parentView?.snackbar(context.stringResource(R.string.note_is_deleted), anchorView = parentAnchorView)
                    navController?.popBackStack(args.destination, false)
                    if (viewModel.note.value.reminderDate != null)
                        alarmManager?.cancelAlarm(context, viewModel.note.value.id)
                    viewModel.deleteNote().invokeOnCompletion { dismiss() }
                }

                navController?.navigateSafely(
                    NoteDialogFragmentDirections.actionNoteDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                        clickListener,
                    )
                )
            }
        }
    }

    private fun NoteDialogFragmentBinding.setupNote(note: Note) {
        context?.let { context ->
            if (note.isPinned) {
                tvPinNote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_off_24, 0, 0, 0)
                tvPinNote.text = context.stringResource(R.string.unpin_note)
            } else {
                tvPinNote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_24, 0, 0, 0)
                tvPinNote.text = context.stringResource(R.string.pin_note)
            }

            if (note.isArchived) {
                tvArchiveNote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_unarchive_24, 0, 0, 0)
                tvArchiveNote.text = context.stringResource(R.string.unarchive_note)
            } else {
                tvArchiveNote.text = context.stringResource(R.string.archive_note)
                tvArchiveNote.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_archive_24, 0, 0, 0)
            }

            if (note.reminderDate == null) {
                tvRemindMe.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_notification_add_24, 0, 0, 0)
                tvRemindMe.text = context.stringResource(R.string.add_note_reminder)
            } else {
                tvRemindMe.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_edit_notifications_24, 0, 0, 0)
                tvRemindMe.text = context.stringResource(R.string.edit_note_reminder)
            }
        }
    }

    private fun NoteDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            baseDialogFragment.tvDialogTitle.setTextColor(context.colorResource(library.color.toResource()))
            baseDialogFragment.vHead.backgroundTintList = context.colorStateResource(library.color.toResource())
            listOf(
                tvCopyToClipboard, tvCopyNote, tvOpenInReadingMode, tvShareNote, tvArchiveNote,
                tvDuplicateNote, tvPinNote, tvRemindMe, tvDeleteNote, tvMoveNote,
            ).forEach { tv -> TextViewCompat.setCompoundDrawableTintList(tv, context.colorStateResource(library.color.toResource())) }
        }
    }

}