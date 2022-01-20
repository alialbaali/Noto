package com.noto.app.library

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryDialogFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupListeners()
            setupState(baseDialogFragment)
        }

    private fun LibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            tvDialogTitle.text = if (args.libraryId == Library.InboxId)
                context?.stringResource(R.string.inbox_options)
            else
                context?.stringResource(R.string.library_options)
        }

    private fun LibraryDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.library
            .onEach { library -> setupLibrary(library, baseDialogFragment) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryDialogFragmentBinding.setupListeners() {
        val parentView = parentFragment?.view

        tvEditLibrary.setOnClickListener {
            dismiss()
            navController?.navigateSafely(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToNewLibraryDialogFragment(args.libraryId))
        }

        tvNewNoteShortcut.setOnClickListener {
            context?.let { context ->
                if (ShortcutManagerCompat.isRequestPinShortcutSupported(context))
                    ShortcutManagerCompat.requestPinShortcut(context, context.createPinnedShortcut(viewModel.library.value), null)
            }
            dismiss()
        }

        tvArchiveLibrary.setOnClickListener {
            if (viewModel.library.value.isVaulted) {
                context?.let { context ->
                    val confirmationText = context.stringResource(R.string.archive_vaulted_library_confirmation)
                    val descriptionText = context.stringResource(R.string.archive_vaulted_library_description)
                    val btnText = context.stringResource(R.string.archive_library)
                    setupArchiveVaultedLibraryConfirmationDialog()
                    navController?.navigateSafely(
                        LibraryDialogFragmentDirections.actionLibraryDialogFragmentToConfirmationDialogFragment(
                            confirmationText,
                            descriptionText,
                            btnText,
                        )
                    )
                }
            } else {
                viewModel.toggleLibraryIsArchived().invokeOnCompletion {
                    val resource = if (viewModel.library.value.isArchived)
                        R.string.library_is_unarchived
                    else
                        R.string.library_is_archived

                    context?.let { context ->
                        context.updateAllWidgetsData()
                        parentView?.snackbar(context.stringResource(resource), viewModel.library.value)
                    }
                    dismiss()
                }
            }
        }

        tvVaultLibrary.setOnClickListener {
            viewModel.toggleLibraryIsVaulted().invokeOnCompletion {
                val resource = if (viewModel.library.value.isVaulted)
                    R.string.library_is_unvaulted
                else
                    R.string.library_is_vaulted

                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(resource), viewModel.library.value)
                }
                dismiss()
            }
        }

        tvPinLibrary.setOnClickListener {
            viewModel.toggleLibraryIsPinned().invokeOnCompletion {
                val resource = if (viewModel.library.value.isPinned)
                    R.string.library_is_unpinned
                else
                    R.string.library_is_pinned

                context?.let { context ->
                    context.updateAllWidgetsData()
                    parentView?.snackbar(context.stringResource(resource), viewModel.library.value)
                }
                dismiss()
            }
        }

        tvChangeParent.setOnClickListener {
            navController?.currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Long>(Constants.LibraryId)
                ?.observe(viewLifecycleOwner) { libraryId ->
                    viewModel.updateLibraryParentId(libraryId).invokeOnCompletion {
                        dismiss()
                    }
                }
            navController?.navigateSafely(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToSelectLibraryDialogFragment(args.libraryId))
        }

        tvDeleteLibrary.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_library_confirmation)
                val descriptionText = context.stringResource(R.string.delete_library_description)
                val btnText = context.stringResource(R.string.delete_library)
                setupDeleteLibraryConfirmationDialog()
                navController?.navigateSafely(
                    LibraryDialogFragmentDirections.actionLibraryDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
        }
    }

    private fun LibraryDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            val colorStateList = color.toColorStateList()
            baseDialogFragment.vHead.background?.mutate()?.setTint(color)
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            listOf(divider1, divider2, divider3).forEach { divider ->
                divider.root.background?.mutate()?.setTint(color.withDefaultAlpha())
            }
            listOf(tvEditLibrary, tvArchiveLibrary, tvVaultLibrary, tvPinLibrary, tvChangeParent, tvNewNoteShortcut, tvDeleteLibrary)
                .forEach { tv ->
                    TextViewCompat.setCompoundDrawableTintList(tv, colorStateList)
                    tv.background.setRippleColor(colorStateList)
                }

            if (library.isInbox) {
                divider1.root.isVisible = false
                divider2.root.isVisible = false
                tvArchiveLibrary.isVisible = false
                tvVaultLibrary.isVisible = false
                tvDeleteLibrary.isVisible = false
                tvPinLibrary.isVisible = false
                tvChangeParent.isVisible = false
                tvEditLibrary.text = context.stringResource(R.string.edit_inbox)
            }

            if (library.isInbox) {
                tvArchiveLibrary.isVisible = false
                tvVaultLibrary.isVisible = false
                tvDeleteLibrary.isVisible = false
                tvPinLibrary.isVisible = false
                tvEditLibrary.text = context.stringResource(R.string.edit_inbox)
            }

            if (library.isArchived) {
                tvArchiveLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_unarchive_24, 0, 0, 0)
                tvArchiveLibrary.text = context.stringResource(R.string.unarchive_library)
            } else {
                tvArchiveLibrary.text = context.stringResource(R.string.archive_library)
                tvArchiveLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_archive_24, 0, 0, 0)
            }

            if (library.isVaulted) {
                tvVaultLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_lock_open_24, 0, 0, 0)
                tvVaultLibrary.text = context.stringResource(R.string.unvault_library)
            } else {
                tvVaultLibrary.text = context.stringResource(R.string.vault_library)
                tvVaultLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_lock_24, 0, 0, 0)
            }

            if (library.isPinned) {
                tvPinLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_off_24, 0, 0, 0)
                tvPinLibrary.text = context.stringResource(R.string.unpin_library)
            } else {
                tvPinLibrary.text = context.stringResource(R.string.pin_library)
                tvPinLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_pin_24, 0, 0, 0)
            }
        }
    }

    private fun setupDeleteLibraryConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val parentView = parentFragment?.view
                val parentAnchorView = parentView?.findViewById<FloatingActionButton>(R.id.fab)
                context?.let { context ->
                    parentView?.snackbar(context.stringResource(R.string.library_is_deleted), viewModel.library.value)
                    context.updateAllWidgetsData()
                    context.updateLibraryListWidgets()
                }
                navController?.popBackStack(R.id.mainFragment, false)
                context?.let { context ->
                    val notes = viewModel.notes.value as? UiState.Success
                    notes?.value
                        ?.filter { entry -> entry.first.reminderDate != null }
                        ?.forEach { entry -> alarmManager?.cancelAlarm(context, entry.first.id) }
                }
                viewModel.deleteLibrary().invokeOnCompletion { dismiss() }
            }
    }

    private fun setupArchiveVaultedLibraryConfirmationDialog() {
        navController?.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) {
                val parentView = parentFragment?.view
                viewModel.toggleLibraryIsArchived().invokeOnCompletion {
                    val resource = if (viewModel.library.value.isArchived)
                        R.string.library_is_unarchived
                    else
                        R.string.library_is_archived

                    context?.let { context ->
                        context.updateAllWidgetsData()
                        context.updateLibraryListWidgets()
                        parentView?.snackbar(context.stringResource(resource), viewModel.library.value)
                    }
                    dismiss()
                }
            }
    }
}