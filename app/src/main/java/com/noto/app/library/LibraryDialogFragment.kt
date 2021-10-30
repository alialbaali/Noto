package com.noto.app.library

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
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
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.library_options)
            }
        }

    private fun LibraryDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.library
            .onEach { library -> setupLibrary(library, baseDialogFragment) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryDialogFragmentBinding.setupListeners() {
        val parentView = parentFragment?.view
        val parentAnchorView = parentView?.findViewById<FloatingActionButton>(R.id.fab)

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
            viewModel.toggleLibraryIsArchived().invokeOnCompletion {
                val resource = if (viewModel.library.value.isArchived)
                    R.string.library_is_unarchived
                else
                    R.string.library_is_archived

                context?.let { context ->
                    parentView?.snackbar(context.stringResource(resource), parentAnchorView)
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
                    parentView?.snackbar(context.stringResource(resource), parentAnchorView)
                }

                dismiss()
            }
        }

        tvDeleteLibrary.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_library_confirmation)
                val descriptionText = context.stringResource(R.string.delete_library_description)
                val btnText = context.stringResource(R.string.delete_library)
                val clickListener = setupConfirmationDialogClickListener()

                navController?.navigateSafely(
                    LibraryDialogFragmentDirections.actionLibraryDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                        clickListener,
                    )
                )
            }
        }
    }

    private fun LibraryDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            val colorState = context.colorStateResource(library.color.toResource())
            baseDialogFragment.vHead.background?.mutate()?.setTint(color)
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            listOf(tvEditLibrary, tvArchiveLibrary, tvPinLibrary, tvNewNoteShortcut, tvDeleteLibrary)
                .forEach { TextViewCompat.setCompoundDrawableTintList(it, colorState) }

            if (library.isArchived) {
                tvArchiveLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_unarchive_24, 0, 0, 0)
                tvArchiveLibrary.text = context.stringResource(R.string.unarchive_library)
            } else {
                tvArchiveLibrary.text = context.stringResource(R.string.archive_library)
                tvArchiveLibrary.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_round_archive_24, 0, 0, 0)
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

    private fun setupConfirmationDialogClickListener() = ConfirmationDialogFragment.ConfirmationDialogClickListener {
        val parentView = parentFragment?.view
        val parentAnchorView = parentView?.findViewById<FloatingActionButton>(R.id.fab)
        context?.let { context ->
            parentView?.snackbar(context.stringResource(R.string.library_is_deleted), anchorView = parentAnchorView)
        }
        navController?.popBackStack(R.id.mainFragment, false)
        viewModel.notes.value
            .filter { entry -> entry.first.reminderDate != null }
            .forEach { entry ->
                context?.let { context ->
                    alarmManager?.cancelAlarm(context, entry.first.id)
                }
            }
        viewModel.deleteLibrary().invokeOnCompletion { dismiss() }
    }
}