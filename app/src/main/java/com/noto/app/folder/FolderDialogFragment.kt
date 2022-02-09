package com.noto.app.folder

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FolderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupListeners()
            setupState(baseDialogFragment)
        }

    private fun FolderDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply { tvDialogTitle.text = context?.stringResource(R.string.folder_options) }

    private fun FolderDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.folder
            .onEach { folder -> setupFolder(folder, baseDialogFragment) }
            .launchIn(lifecycleScope)
    }

    private fun FolderDialogFragmentBinding.setupListeners() {
        val parentView = parentFragment?.view

        tvEditFolder.setOnClickListener {
            dismiss()
            navController?.navigateSafely(FolderDialogFragmentDirections.actionFolderDialogFragmentToNewFolderDialogFragment(args.folderId))
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
                viewModel.toggleFolderIsArchived().invokeOnCompletion {
                    val resource = if (viewModel.folder.value.isArchived)
                        R.string.folder_is_unarchived
                    else
                        R.string.folder_is_archived

                    context?.let { context ->
                        context.updateAllWidgetsData()
                        parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                    }
                    dismiss()
                }
            }
        }

        tvVaultFolder.setOnClickListener {
            viewModel.toggleFolderIsVaulted().invokeOnCompletion {
                val resource = if (viewModel.folder.value.isVaulted)
                    R.string.folder_is_unvaulted
                else
                    R.string.folder_is_vaulted

                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                }
                dismiss()
            }
        }

        tvPinFolder.setOnClickListener {
            viewModel.toggleFolderIsPinned().invokeOnCompletion {
                val resource = if (viewModel.folder.value.isPinned)
                    R.string.folder_is_unpinned
                else
                    R.string.folder_is_pinned

                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                }
                dismiss()
            }
        }

        tvChangeParent.setOnClickListener {
            navController?.currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Long>(Constants.FolderId)
                ?.observe(viewLifecycleOwner) { folderId ->
                    viewModel.updateFolderParentId(folderId).invokeOnCompletion {
                        dismiss()
                    }
                }
            navController?.navigateSafely(
                FolderDialogFragmentDirections.actionFolderDialogFragmentToSelectFolderDialogFragment(
                    longArrayOf(args.folderId, Folder.GeneralFolderId),
                    selectedFolderId = viewModel.folder.value.parentId ?: 0L,
                    isNoParentEnabled = true
                )
            )
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

    private fun FolderDialogFragmentBinding.setupFolder(folder: Folder, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            baseDialogFragment.vHead.background?.mutate()?.setTint(color)
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            listOf(divider1, divider2, divider3).forEach { divider ->
                divider.root.background?.mutate()?.setTint(color.withDefaultAlpha())
            }
            listOf(tvEditFolder, tvArchiveFolder, tvVaultFolder, tvPinFolder, tvChangeParent, tvNewNoteShortcut, tvDeleteFolder)
                .forEach { tv ->
                    TextViewCompat.setCompoundDrawableTintList(tv, colorStateList)
                    tv.background.setRippleColor(colorStateList)
                }

            if (folder.isGeneral) {
                divider1.root.isVisible = false
                divider2.root.isVisible = false
                tvArchiveFolder.isVisible = false
                tvVaultFolder.isVisible = false
                tvDeleteFolder.isVisible = false
                tvPinFolder.isVisible = false
                tvChangeParent.isVisible = false
            }

            if (folder.isArchived) {
                tvArchiveFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_unarchive_24, 0, 0, 0)
                tvArchiveFolder.text = context.stringResource(R.string.unarchive_folder)
            } else {
                tvArchiveFolder.text = context.stringResource(R.string.archive_folder)
                tvArchiveFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_archive_24, 0, 0, 0)
            }

            if (folder.isVaulted) {
                tvVaultFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_lock_open_24, 0, 0, 0)
                tvVaultFolder.text = context.stringResource(R.string.unvault_folder)
            } else {
                tvVaultFolder.text = context.stringResource(R.string.vault_folder)
                tvVaultFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_lock_24, 0, 0, 0)
            }

            if (folder.isPinned) {
                tvPinFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_off_24, 0, 0, 0)
                tvPinFolder.text = context.stringResource(R.string.unpin_folder)
            } else {
                tvPinFolder.text = context.stringResource(R.string.pin_folder)
                tvPinFolder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_24, 0, 0, 0)
            }
        }
    }

    private fun setupDeleteFolderConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val parentView = parentFragment?.view
                val selectedFolderId = navController?.getBackStackEntry(R.id.folderFragment)?.arguments?.getLong(Constants.FolderId)
                if (selectedFolderId == viewModel.folder.value.id) {
                    val args = bundleOf(Constants.FolderId to Folder.GeneralFolderId)
                    val options = navOptions {
                        popUpTo(R.id.folderFragment) {
                            inclusive = true
                        }
                    }
                    navController?.navigate(R.id.folderFragment, args, options)
                }

                context?.let { context ->
                    parentView?.snackbar(context.stringResource(R.string.folder_is_deleted), viewModel.folder.value)
                    context.updateAllWidgetsData()
                    context.updateFolderListWidgets()
                    val notes = viewModel.notes.value as? UiState.Success
                    notes?.value
                        ?.filter { entry -> entry.first.reminderDate != null }
                        ?.forEach { entry -> alarmManager?.cancelAlarm(context, entry.first.id) }
                }
                viewModel.deleteFolder().invokeOnCompletion { dismiss() }
            }
    }

    private fun setupArchiveVaultedFolderConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val parentView = parentFragment?.view
                viewModel.toggleFolderIsArchived().invokeOnCompletion {
                    val resource = if (viewModel.folder.value.isArchived)
                        R.string.folder_is_unarchived
                    else
                        R.string.folder_is_archived

                    context?.let { context ->
                        context.updateAllWidgetsData()
                        context.updateFolderListWidgets()
                        parentView?.snackbar(context.stringResource(resource), viewModel.folder.value)
                    }
                    dismiss()
                }
            }
    }
}