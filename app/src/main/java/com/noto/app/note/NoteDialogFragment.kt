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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.note_options)
        }
        setupListeners()
        collectState(baseDialog)
    }

    private fun NoteDialogFragmentBinding.collectState(baseDialog: BaseDialogFragmentBinding) {
        viewModel.library
            .onEach {
                baseDialog.tvDialogTitle.setTextColor(colorResource(it.color.toResource()))
                baseDialog.vHead.backgroundTintList = colorStateResource(it.color.toResource())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    listOf(tvCopyToClipboard, tvShareNote, tvArchiveNote, tvRemindMe)
                        .forEach { tv -> tv.compoundDrawableTintList = colorStateResource(it.color.toResource()) }
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach {
                if (it.isArchived) {
                    tvArchiveNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_unarchive_24, 0, 0, 0)
                    tvArchiveNote.text = stringResource(R.string.unarchive_note)
                } else {
                    tvArchiveNote.text = stringResource(R.string.archive_note)
                    tvArchiveNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.archive_arrow_down_outline, 0, 0, 0)
                }

                if (it.reminderDate == null) {
                    tvRemindMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bell_plus_outline, 0, 0, 0)
                    tvRemindMe.text = stringResource(R.string.add_reminder)
                } else {
                    tvRemindMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bell_ring_outline, 0, 0, 0)
                    tvRemindMe.text = stringResource(R.string.edit_reminder)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteDialogFragmentBinding.setupListeners() {

        tvArchiveNote.setOnClickListener {
            dismiss()
            viewModel.toggleNoteIsArchived()

            val resource = if (viewModel.note.value.isArchived)
                R.string.note_unarchived
            else
                R.string.note_archived

            requireParentFragment()
                .requireView()
                .apply {
                    val anchorView = findViewById<FloatingActionButton>(R.id.fab)
                    snackbar(stringResource(resource), anchorView)
                }
        }

        tvRemindMe.setOnClickListener {
            dismiss()
            findNavController().navigate(NoteDialogFragmentDirections.actionNotoDialogFragmentToReminderDialogFragment(args.noteId))
        }

        tvCopyToClipboard.setOnClickListener { v ->
            dismiss()
            val clipData = ClipData.newPlainText(viewModel.library.value.title, viewModel.note.value.format())
            clipboardManager.setPrimaryClip(clipData)
            v.toast(getString(R.string.copied_to_clipboard))
        }

        tvShareNote.setOnClickListener {
            dismiss()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_MIME_TYPES, viewModel.note.value.format())
            }
            val chooser = Intent.createChooser(intent, getString(R.string.share_note))
            startActivity(chooser)
        }

        tvDeleteNote.setOnClickListener {
            dismiss()
            val title = getString(R.string.delete_note_confirmation)
            val btnText = getString(R.string.delete_note)
            val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                viewModel.deleteNote()
            }

            findNavController().navigate(
                NoteDialogFragmentDirections.actionNotoDialogFragmentToConfirmationDialogFragment(
                    title,
                    null,
                    btnText,
                    clickListener,
                )
            )
        }

    }
}