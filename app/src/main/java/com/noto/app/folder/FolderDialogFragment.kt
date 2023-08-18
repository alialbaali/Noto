package com.noto.app.folder

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.FolderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<FolderDialogFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val anchorViewId by lazy { R.id.bab }

    private val parentView by lazy { parentFragment?.view }

    private val folderColor by lazy { viewModel.folder.value.color }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FolderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun FolderDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.folder_options)
        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)
    }

    private fun FolderDialogFragmentBinding.setupListeners() {
        tvEditFolder.setOnClickListener {
            navController?.navigateSafely(FolderDialogFragmentDirections.actionFolderDialogFragmentToNewFolderFragment(args.folderId))
            dismiss()
        }

        tvNewNoteShortcut.setOnClickListener {
            context?.let { context ->
                if (ShortcutManagerCompat.isRequestPinShortcutSupported(context))
                    ShortcutManagerCompat.requestPinShortcut(context, context.createPinnedShortcut(viewModel.folder.value), null)
            }
            dismiss()
        }

        tvArchiveFolder.setOnClickListener {
            if (viewModel.folder.value.isVaulted) {
                context?.let { context ->
                    val confirmationText = context.stringResource(R.string.archive_vaulted_folder_confirmation)
                    val descriptionText = context.stringResource(R.string.archive_vaulted_folder_description)
                    val btnText = context.stringResource(R.string.archive_folder)
                    setupArchiveVaultedFolderConfirmationDialog()
                    navController?.navigateSafely(
                        FolderDialogFragmentDirections.actionFolderDialogFragmentToConfirmationDialogFragment(
                            confirmationText,
                            descriptionText,
                            btnText,
                        )
                    )
                }
            } else {
                val isArchived = viewModel.folder.value.isArchived
                viewModel.toggleFolderIsArchived().invokeOnCompletion {
                    val stringId = if (isArchived)
                        R.string.folder_is_unarchived
                    else
                        R.string.folder_is_archived
                    val drawableId = if (isArchived)
                        R.drawable.ic_round_unarchive_24
                    else
                        R.drawable.ic_round_archive_24
                    context?.let { context ->
                        context.updateAllWidgetsData()
                        parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                    }
                    dismiss()
                }
            }
        }

        tvVaultFolder.setOnClickListener {
            val isVaulted = viewModel.folder.value.isVaulted
            viewModel.toggleFolderIsVaulted().invokeOnCompletion {
                val stringId = if (isVaulted)
                    R.string.folder_is_unvaulted
                else
                    R.string.folder_is_vaulted
                val drawableId = if (isVaulted)
                    R.drawable.ic_round_lock_open_24
                else
                    R.drawable.ic_round_lock_24
                context?.let { context ->
                    parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                    context.updateAllWidgetsData()
                }
                dismiss()
            }
        }

        tvPinFolder.setOnClickListener {
            val isPinned = viewModel.folder.value.isPinned
            viewModel.toggleFolderIsPinned().invokeOnCompletion {
                val stringId = if (isPinned)
                    R.string.folder_is_unpinned
                else
                    R.string.folder_is_pinned
                val drawableId = if (isPinned)
                    R.drawable.ic_round_pin_off_24
                else
                    R.drawable.ic_round_pin_24
                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                }
                dismiss()
            }
        }

        tvDeleteFolder.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_folder_confirmation)
                val descriptionText = context.stringResource(R.string.delete_folder_description)
                val btnText = context.stringResource(R.string.delete_folder)
                setupDeleteFolderConfirmationDialog()
                navController?.navigateSafely(
                    FolderDialogFragmentDirections.actionFolderDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
        }
    }

    private fun FolderDialogFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toColorResourceId())
            val colorStateList = color.toColorStateList()
            tb.vHead.background?.mutate()?.setTint(color)
            tb.tvDialogTitle.setTextColor(color)
            listOf(tvEditFolder, tvArchiveFolder, tvVaultFolder, tvPinFolder, tvNewNoteShortcut, tvDeleteFolder)
                .forEach { tv ->
                    tv.background.setRippleColor(colorStateList)
                }

            if (folder.isGeneral) {
                vFolder.ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_general_24))
                divider.root.isVisible = false
                tvArchiveFolder.isVisible = false
                tvVaultFolder.isVisible = false
                tvDeleteFolder.isVisible = false
                tvPinFolder.isVisible = false
            } else {
                vFolder.ivFolderIcon.setImageDrawable(context.drawableResource(R.drawable.ic_round_folder_24))
            }

            vFolder.ibFolderHandle.isVisible = false
            vFolder.tvFolderTitle.text = folder.getTitle(context)
            vFolder.tvFolderTitle.setTextColor(color)
            vFolder.ivFolderIcon.imageTintList = colorStateList

            if (folder.isArchived) {
                tvArchiveFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_unarchive_24, 0, 0)
                tvArchiveFolder.text = context.stringResource(R.string.unarchive)
            } else {
                tvArchiveFolder.text = context.stringResource(R.string.archive)
                tvArchiveFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_archive_24, 0, 0)
            }

            if (folder.isVaulted) {
                tvVaultFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_lock_open_24, 0, 0)
                tvVaultFolder.text = context.stringResource(R.string.remove_from_vault)
            } else {
                tvVaultFolder.text = context.stringResource(R.string.add_to_vault)
                tvVaultFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_lock_24, 0, 0)
            }

            if (folder.isPinned) {
                tvPinFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_pin_off_24, 0, 0)
                tvPinFolder.text = context.stringResource(R.string.unpin)
            } else {
                tvPinFolder.text = context.stringResource(R.string.pin)
                tvPinFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_round_pin_24, 0, 0)
            }
        }
    }

    private fun setupDeleteFolderConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val stringId = R.string.folder_is_deleted
                val drawableId = R.drawable.ic_round_delete_sweep_24
                val selectedFolderId = navController?.getBackStackEntry(R.id.folderFragment)?.arguments?.getLong(Constants.FolderId)
                val notes = viewModel.notes.value as? UiState.Success
                context?.let { context ->
                    context.updateAllWidgetsData()
                    context.updateFolderListWidgets()
                    parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                    notes?.value
                        ?.filter { model -> model.note.reminderDate != null }
                        ?.forEach { model -> alarmManager?.cancelAlarm(context, model.note.id) }
                }
                viewModel.deleteFolder().invokeOnCompletion { dismiss() }
                if (selectedFolderId == viewModel.folder.value.id) {
                    navController?.navigateUp() // Dismiss ConfirmationDialogFragment
                    navController?.navigateSafely(FolderDialogFragmentDirections.actionFolderDialogFragmentToFolderFragment(folderId = Folder.GeneralFolderId)) {
                        popUpTo(R.id.folderFragment) {
                            inclusive = true
                        }
                    }
                }
            }
    }

    private fun setupArchiveVaultedFolderConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val parentView = parentFragment?.view
                val isArchived = viewModel.folder.value.isArchived
                viewModel.toggleFolderIsArchived().invokeOnCompletion {
                    val color = viewModel.folder.value.color
                    val stringId = if (isArchived)
                        R.string.folder_is_unarchived
                    else
                        R.string.folder_is_archived
                    val drawableId = if (isArchived)
                        R.drawable.ic_round_unarchive_24
                    else
                        R.drawable.ic_round_archive_24

                    context?.let { context ->
                        context.updateAllWidgetsData()
                        context.updateFolderListWidgets()
                        parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, color)
                    }
                    dismiss()
                }
            }
    }
}