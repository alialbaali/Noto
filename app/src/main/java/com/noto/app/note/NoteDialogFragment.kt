package com.noto.app.note

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<NoteViewModel>()

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = NoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialog = BaseDialogFragmentBinding.bind(root)

        baseDialog.tvDialogTitle.text = stringResource(R.string.note_options)

        if (args.noteId != 0L) viewModel.getNoteById(args.noteId)
        viewModel.getLibraryById(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner) { library ->
            library?.let {

                baseDialog.tvDialogTitle.setTextColor(colorResource(it.color.toResource()))
                baseDialog.vHead.backgroundTintList = colorStateResource(it.color.toResource())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    listOf(tvCopyToClipboard, tvShareNoto, tvArchiveNoto, tvRemindMe)
                        .forEach { tv -> tv.compoundDrawableTintList = colorStateResource(it.color.toResource()) }

            }

        }

        viewModel.note.observe(viewLifecycleOwner) { noto ->
            noto?.let {

                tvArchiveNoto.compoundDrawablesRelative[0] =
                    if (noto.isArchived) drawableResource(R.drawable.ic_outline_unarchive_24)
                    else drawableResource(R.drawable.archive_arrow_down_outline)

                tvRemindMe.compoundDrawablesRelative[0] =
                    if (noto.reminderDate == null) drawableResource(R.drawable.bell_plus_outline)
                    else drawableResource(R.drawable.bell_ring_outline)

            }

        }


        tvArchiveNoto.setOnClickListener {
            dismiss()
            if (viewModel.note.value?.isArchived == true) viewModel.setNotoArchived(false) else viewModel.setNotoArchived(true)
            viewModel.updateNote()
        }

        tvRemindMe.setOnClickListener {
            dismiss()
            findNavController().navigate(NoteDialogFragmentDirections.actionNotoDialogFragmentToReminderDialogFragment(args.noteId))
        }

        tvCopyToClipboard.setOnClickListener { v ->
            dismiss()
            val clipData = ClipData.newPlainText(viewModel.library.value?.title, "${viewModel.note.value?.title}\n${viewModel.note.value?.body}")
            clipboardManager.setPrimaryClip(clipData)
            v.toast(getString(R.string.copied_to_clipboard))
        }

        tvShareNoto.setOnClickListener {

            dismiss()

            val noto = viewModel.note.value

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_MIME_TYPES, noto?.title)
                putExtra(Intent.EXTRA_MIME_TYPES, noto?.body)
            }

            val chooser = Intent.createChooser(intent, "${getString(R.string.share)} ${noto?.title} to")

            startActivity(chooser)

        }

        tvDeleteNoto.setOnClickListener {
            dismiss()

            ConfirmationDialogFragment { dialogFragment, dialogBinding ->

                dialogBinding.btnConfirm.text = dialogFragment.getString(R.string.delete_note)
                dialogBinding.tvTitle.text = dialogFragment.getString(R.string.delete_note_confirmation)

                dialogBinding.btnConfirm.setOnClickListener {
                    dialogFragment.dismiss()
                    viewModel.deleteNoto()
                    dialogFragment.findNavController().navigate(
                        R.id.libraryFragment,
                        bundleOf("library_id" to viewModel.library.value?.id),
                        navOptions { popUpTo(R.id.libraryFragment) { inclusive = true } })
                }
            }.show(parentFragmentManager, null)
        }

    }

}