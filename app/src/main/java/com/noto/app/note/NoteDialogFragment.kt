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
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.OpenNotesIn
import com.noto.app.label.noteLabelItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val DefaultQuantity = 1

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager? }

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val isParentReadingMode
        get() = navController?.previousBackStackEntry?.destination?.id == R.id.notePagerFragment

    private val isParentEditor
        get() = navController?.previousBackStackEntry?.destination?.id == R.id.noteFragment

    private val anchorViewId by lazy { R.id.bab }

    private val parentView by lazy { parentFragment?.view }

    private val folderColor by lazy { viewModel.folder.value.color }

    private val selectFolderTitle by lazy {
        when (navController?.previousBackStackEntry?.destination?.id) {
            R.id.folderArchiveFragment -> context?.stringResource(R.string.select_folder_archive)
            else -> context?.stringResource(R.string.select_folder)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupListeners()
        setupState()
    }

    private fun NoteDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.note_options)

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
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
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        tvArchiveNote.setOnClickListener {
            disableSelection()
            viewModel.toggleNoteIsArchived().invokeOnCompletion {
                context?.let { context ->
                    val note = viewModel.note.value
                    if (note.reminderDate != null) alarmManager?.cancelAlarm(context, note.id)
                    val isArchived = note.isArchived
                    val text = if (isArchived)
                        context.stringResource(R.string.note_is_unarchived)
                    else
                        context.quantityStringResource(R.plurals.note_is_archived, DefaultQuantity)
                    val drawableId = if (isArchived)
                        R.drawable.ic_round_unarchive_24
                    else
                        R.drawable.ic_round_archive_24
                    parentView?.snackbar(text, drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                    context.updateNoteListWidgets()
                }
                dismiss()
            }
        }

        tvRemindMe.setOnClickListener {
            disableSelection()
            dismiss()
            navController?.navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReminderDialogFragment(
                    args.folderId,
                    args.noteId
                )
            )
        }

        tvOpenIn.setOnClickListener {
            disableSelection()
            val openNotesIn = viewModel.folder.value.openNotesIn
            when {
                openNotesIn == OpenNotesIn.Editor || isParentEditor -> {
                    navController?.navigateSafely(
                        NoteDialogFragmentDirections.actionNoteDialogFragmentToNotePagerFragment(
                            args.folderId,
                            args.noteId,
                            args.selectedNoteIds,
                        )
                    )
                }

                openNotesIn == OpenNotesIn.ReadingMode || isParentReadingMode -> navController?.navigateSafely(
                    NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteFragment(
                        args.folderId,
                        noteId = args.noteId,
                        selectedNoteIds = args.selectedNoteIds,
                    )
                )
            }
            dismiss()
        }

        tvDuplicateNote.setOnClickListener {
            disableSelection()
            viewModel.duplicateNote().invokeOnCompletion {
                val stringId = R.plurals.note_is_duplicated
                val drawableId = R.drawable.ic_round_control_point_duplicate_24
                context?.let { context ->
                    parentView?.snackbar(context.quantityStringResource(stringId, DefaultQuantity), drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                }
                dismiss()
            }
        }

        tvPinNote.setOnClickListener {
            disableSelection()
            viewModel.toggleNoteIsPinned().invokeOnCompletion {
                val isPinned = viewModel.note.value.isPinned
                context?.let { context ->
                    val stringId = if (isPinned)
                        R.plurals.note_is_unpinned
                    else
                        R.plurals.note_is_pinned
                    val drawableId = if (isPinned)
                        R.drawable.ic_round_pin_off_24
                    else
                        R.drawable.ic_round_pin_24
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.quantityStringResource(stringId, DefaultQuantity), drawableId, anchorViewId, folderColor)
                }
                dismiss()
            }
        }

        tvCopyToClipboard.setOnClickListener {
            disableSelection()
            context?.let { context ->
                val clipData = ClipData.newPlainText(viewModel.folder.value.getTitle(context), viewModel.note.value.format())
                clipboardManager?.setPrimaryClip(clipData)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    val stringId = R.plurals.note_copied_to_clipboard
                    val drawableId = R.drawable.ic_round_copy_24
                    parentView?.snackbar(context.quantityStringResource(stringId, DefaultQuantity), drawableId, anchorViewId, folderColor)
                }
            }
            dismiss()
        }

        tvCopyNote.setOnClickListener {
            disableSelection()
            savedStateHandle?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.copyNote(folderId).invokeOnCompletion {
                        val stringId = R.plurals.note_is_copied
                        val drawableId = R.drawable.ic_round_file_copy_24
                        val folderTitle = savedStateHandle.get<String>(Constants.FolderTitle)
                        context?.let { context ->
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                            parentView?.snackbar(
                                context.quantityStringResource(stringId, DefaultQuantity, folderTitle),
                                drawableId,
                                anchorViewId,
                                folderColor
                            )
                        }
                        navController?.popBackStack(args.destination, false)
                        dismiss()
                    }
                }
            context?.let { context ->
                navController?.navigateSafely(
                    NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectFolderDialogFragment(
                        filteredFolderIds = longArrayOf(args.folderId),
                        title = context.stringResource(R.string.copy_to).removeSuffix("…")
                    )
                )
            }
        }

        tvMoveNote.setOnClickListener {
            disableSelection()
            savedStateHandle?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.moveNote(folderId).invokeOnCompletion {
                        val stringId = R.plurals.note_is_moved
                        val drawableId = R.drawable.ic_round_move_24
                        val folderTitle = savedStateHandle.get<String>(Constants.FolderTitle)
                        context?.let { context ->
                            parentView?.snackbar(
                                context.quantityStringResource(stringId, DefaultQuantity, folderTitle),
                                drawableId,
                                anchorViewId,
                                folderColor
                            )
                            context.updateAllWidgetsData()
                            context.updateNoteListWidgets()
                        }
                        navController?.popBackStack(args.destination, false)
                        dismiss()
                    }
                }
            context?.let { context ->
                navController?.navigateSafely(
                    NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectFolderDialogFragment(
                        filteredFolderIds = longArrayOf(args.folderId),
                        title = context.stringResource(R.string.move_to).removeSuffix("…")
                    )
                )
            }
        }

        tvShareNote.setOnClickListener {
            disableSelection()
            dismiss()
            launchShareNotesIntent(listOf(viewModel.note.value))
        }

        tvDeleteNote.setOnClickListener {
            disableSelection()
            context?.let { context ->
                val confirmationText = context.quantityStringResource(R.plurals.delete_note_confirmation, DefaultQuantity)
                val descriptionText = context.quantityStringResource(R.plurals.delete_note_description, DefaultQuantity)
                val btnText = context.quantityStringResource(R.plurals.delete_note, DefaultQuantity)
                savedStateHandle?.getLiveData<Int>(Constants.ClickListener)
                    ?.observe(viewLifecycleOwner) {
                        val stringId = R.plurals.note_is_deleted
                        val drawableId = R.drawable.ic_round_delete_24
                        parentView?.snackbar(context.quantityStringResource(stringId, DefaultQuantity), drawableId, anchorViewId, folderColor)
                        navController?.popBackStack(args.destination, false)
                        if (viewModel.note.value.reminderDate != null) alarmManager?.cancelAlarm(context, viewModel.note.value.id)
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
            vNote.tvAccessDate.text = context.stringResource(R.string.accessed, note.accessDate.format(context))
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

            if (isCurrentLocaleArabic()) {
                vNote.tvCreationDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold)
                vNote.tvAccessDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold)
            } else {
                vNote.tvCreationDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
                vNote.tvAccessDate.typeface = root.context?.tryLoadingFontResource(R.font.nunito_semibold_italic)
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

    private fun disableSelection() {
        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.DisableSelection, true)
    }

    private fun NoteDialogFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            tb.tvDialogTitle.setTextColor(color)
            tb.vHead.background?.mutate()?.setTint(color)
            when {
                folder.openNotesIn == OpenNotesIn.Editor || isParentEditor -> {
                    tvOpenIn.text = context.stringResource(R.string.reading_mode)
                    tvOpenIn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_reading_mode_24, 0, 0)
                }

                folder.openNotesIn == OpenNotesIn.ReadingMode || isParentReadingMode -> {
                    tvOpenIn.text = context.stringResource(R.string.edit)
                    tvOpenIn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_edit_24, 0, 0)
                }
            }
            listOf(
                tvCopyToClipboard, tvCopyNote, tvOpenIn, tvShareNote, tvArchiveNote,
                tvDuplicateNote, tvPinNote, tvRemindMe, tvDeleteNote, tvMoveNote,
            ).forEach { tv ->
                tv.background.setRippleColor(colorStateList)
            }
        }
    }

}