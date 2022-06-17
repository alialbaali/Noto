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
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note
import com.noto.app.label.noteLabelItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        viewModel.folder
            .onEach { folder -> setupFolder(folder, baseDialogFragment) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.note,
            viewModel.labels.map { it.filterSelected() },
        ) { folder, note, labels ->
            setupNote(folder, note, labels)
        }.launchIn(lifecycleScope)
    }

    private fun NoteDialogFragmentBinding.setupListeners() {
        val parentView = parentFragment?.view

        tvArchiveNote.setOnClickListener {
            viewModel.toggleNoteIsArchived().invokeOnCompletion {
                val resource = if (viewModel.note.value.isArchived)
                    R.string.note_is_unarchived
                else
                    R.string.note_is_archived

                context?.let { context ->
                    context.updateAllWidgetsData()
                    context.updateNoteListWidgets()
                    parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                }
                dismiss()
            }
        }

        tvRemindMe.setOnClickListener {
            dismiss()
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReminderDialogFragment(
                    args.folderId,
                    args.noteId
                )
            )
        }

        tvOpenInReadingMode.setOnClickListener {
            dismiss()
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReadingModeFragment(
                    args.folderId,
                    args.noteId
                )
            )
        }

        tvDuplicateNote.setOnClickListener {
            viewModel.duplicateNote().invokeOnCompletion {
                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(R.string.note_is_duplicated), viewModel.folder.value)
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
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                }
                dismiss()
            }
        }

        tvCopyToClipboard.setOnClickListener {
            context?.let { context ->
                val clipData = ClipData.newPlainText(viewModel.folder.value.getTitle(context), viewModel.note.value.format())
                clipboardManager?.setPrimaryClip(clipData)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                    parentView?.snackbar(context.stringResource(R.string.note_copied_to_clipboard), viewModel.folder.value)
            }
            dismiss()
        }

        tvCopyNote.setOnClickListener {
            navController?.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.copyNote(folderId).invokeOnCompletion {
                        context?.let { context ->
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            parentView?.snackbar(context.stringResource(R.string.note_is_copied), viewModel.folder.value)
                        }
                        navController?.popBackStack(args.destination, false)
                        dismiss()
                    }
                }
            navController?.navigateSafely(NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectFolderDialogFragment(longArrayOf(args.folderId)))
        }

        tvMoveNote.setOnClickListener {
            navController?.currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.moveNote(folderId).invokeOnCompletion {
                        context?.let { context ->
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            parentView?.snackbar(context.stringResource(R.string.note_is_moved), viewModel.folder.value)
                        }
                        navController?.popBackStack(args.destination, false)
                        dismiss()
                    }
                }
            navController?.navigateSafely(NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectFolderDialogFragment(longArrayOf(args.folderId)))
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
                navController?.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Int>(Constants.ClickListener)
                    ?.observe(viewLifecycleOwner) {
                        parentView?.snackbar(context.stringResource(R.string.note_is_deleted), viewModel.folder.value)
                        navController?.popBackStack(args.destination, false)
                        if (viewModel.note.value.reminderDate != null)
                            alarmManager?.cancelAlarm(context, viewModel.note.value.id)
                        viewModel.deleteNote().invokeOnCompletion {
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            dismiss()
                        }
                    }

                navController?.navigateSafely(
                    NoteDialogFragmentDirections.actionNoteDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
        }
    }

    private fun NoteDialogFragmentBinding.setupNote(folder: Folder, note: Note, labels: List<Label>) {
        context?.let { context ->
            val colorResource = context.colorResource(folder.color.toResource())
            vNote.root.backgroundTintList = context.colorAttributeResource(R.attr.notoBackgroundColor).toColorStateList()
            vNote.ibDrag.isVisible = false
            vNote.tvNoteTitle.text = note.title
            vNote.tvNoteTitle.maxLines = 3
            vNote.tvNoteTitle.isVisible = note.title.isNotBlank()
            vNote.tvNoteTitle.setLinkTextColor(colorResource)
            vNote.tvNoteTitle.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMarginsRelative(bottom = if (note.body.isBlank()) 0.dp else 4.dp)
            }
            vNote.tvNoteBody.text = note.body
            vNote.tvNoteBody.maxLines = 5
            vNote.tvNoteBody.isVisible = note.body.isNotBlank()
            vNote.tvNoteBody.setLinkTextColor(colorResource)
            vNote.tvNoteBody.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMarginsRelative(top = if (note.title.isBlank()) 0.dp else 4.dp)
            }
            vNote.tvCreationDate.text = context.stringResource(R.string.created, note.creationDate.format(context))
            vNote.tvCreationDate.isVisible = true
            vNote.tvAccessDate.text = context.stringResource(R.string.accessed, note.accessDate?.format(context))
            vNote.tvAccessDate.isVisible = true
            if (note.reminderDate != null) {
                vNote.llReminder.background?.mutate()?.setTint(colorResource)
                vNote.tvReminder.text = note.reminderDate.format(context)
            }
            vNote.llReminder.isVisible = note.reminderDate != null
            vNote.rv.isVisible = labels.isNotEmpty()
            vNote.rv.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
            vNote.rv.withModels {
                labels.forEach { label ->
                    noteLabelItem {
                        id(label.id)
                        label(label)
                        color(folder.color)
                    }
                }
            }

            if (note.isPinned) {
                tvPinNote.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_pin_off_24, 0, 0)
                tvPinNote.text = context.stringResource(R.string.unpin)
            } else {
                tvPinNote.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_pin_24, 0, 0)
                tvPinNote.text = context.stringResource(R.string.pin)
            }

            if (note.isArchived) {
                tvArchiveNote.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_unarchive_24, 0, 0)
                tvArchiveNote.text = context.stringResource(R.string.unarchive)
            } else {
                tvArchiveNote.text = context.stringResource(R.string.archive)
                tvArchiveNote.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_archive_24, 0, 0)
            }

            if (note.reminderDate == null) {
                tvRemindMe.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_notification_add_24, 0, 0)
                tvRemindMe.text = context.stringResource(R.string.add_note_reminder)
            } else {
                tvRemindMe.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_edit_notifications_24, 0, 0)
                tvRemindMe.text = context.stringResource(R.string.edit_note_reminder)
            }
        }
    }

    private fun NoteDialogFragmentBinding.setupFolder(folder: Folder, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            baseDialogFragment.vHead.backgroundTintList = colorStateList
            listOf(
                tvCopyToClipboard, tvCopyNote, tvOpenInReadingMode, tvShareNote, tvArchiveNote,
                tvDuplicateNote, tvPinNote, tvRemindMe, tvDeleteNote, tvMoveNote,
            ).forEach { tv ->
                tv.background.setRippleColor(colorStateList)
            }
        }
    }

}