package com.noto.app.note

import android.app.Activity
import android.app.AlarmManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PdfGenerator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.widget.TextViewCompat
import androidx.documentfile.provider.DocumentFile
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
import java.io.File

private const val SelectDirectoryRequestCode = 1

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

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
        tvDialogTitle.text = resources.stringResource(R.string.note_options)
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
        val parentView = requireParentFragment().requireView()
        val parentAnchorView = parentView.findViewById<FloatingActionButton>(R.id.fab)

        tvArchiveNote.setOnClickListener {
            dismiss()
            viewModel.toggleNoteIsArchived()

            val resource = if (viewModel.note.value.isArchived)
                R.string.note_is_unarchived
            else
                R.string.note_is_archived

            parentView.snackbar(resources.stringResource(resource), parentAnchorView)
        }

        tvRemindMe.setOnClickListener {
            dismiss()
            findNavController().navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReminderDialogFragment(
                    args.libraryId,
                    args.noteId
                )
            )
        }

        tvOpenInReadingMode.setOnClickListener {
            dismiss()
            findNavController().navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToNoteReadingModeFragment(
                    args.libraryId,
                    args.noteId
                )
            )
        }

        tvDuplicateNote.setOnClickListener {
            viewModel.duplicateNote().invokeOnCompletion {
                dismiss()
                parentView.snackbar(resources.stringResource(R.string.note_is_duplicated), parentAnchorView)
            }
        }

        tvPinNote.setOnClickListener {
            dismiss()
            viewModel.toggleNoteIsPinned()
            val resource = if (viewModel.note.value.isPinned)
                R.string.note_is_unpinned
            else
                R.string.note_is_pinned
            parentView.snackbar(resources.stringResource(resource), parentAnchorView)
        }

        tvCopyToClipboard.setOnClickListener {
            dismiss()
            val clipData = ClipData.newPlainText(viewModel.library.value.title, viewModel.note.value.format())
            clipboardManager.setPrimaryClip(clipData)
            parentView.snackbar(getString(R.string.note_copied_to_clipboard), anchorView = parentAnchorView)
        }

        tvCopyNote.setOnClickListener {
            val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener {
                viewModel.copyNote(it).invokeOnCompletion {
                    parentView.snackbar(resources.stringResource(R.string.note_is_copied), anchorView = parentAnchorView)
                    findNavController().popBackStack(args.destination, false)
                    dismiss()
                }
            }
            findNavController().navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToSelectLibraryDialogFragment(
                    selectLibraryItemClickListener,
                    args.libraryId
                )
            )
        }

        tvMoveNote.setOnClickListener {
            val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener {
                viewModel.moveNote(it).invokeOnCompletion {
                    parentView.snackbar(resources.stringResource(R.string.note_is_moved), anchorView = parentAnchorView)
                    findNavController().popBackStack(args.destination, false)
                    dismiss()
                }
            }
            findNavController().navigateSafely(
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

        tvExportNote.setOnClickListener {
            val outputFile = File(requireContext().externalCacheDir, "example.pdf")
            PdfGenerator.create(outputFile, """
                <strong>${viewModel.note.value.title}</strong>

                ${viewModel.note.value.body}
            """.trimIndent(), requireContext(), object : PdfGenerator.ResultCallback {
                override fun onSuccess(file: File) {
                    val uri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider", file)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "application/pdf"
                        data = uri
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    val chooser = Intent.createChooser(intent, getString(R.string.export_note))
                    startActivity(chooser)
                }

                override fun onFailure(message: String?) {
                }
            })
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
//            startActivityForResult(intent, SelectDirectoryRequestCode)
        }

        tvDeleteNote.setOnClickListener {
            val confirmationText = resources.stringResource(R.string.delete_note_confirmation)
            val descriptionText = resources.stringResource(R.string.delete_note_description)
            val btnText = resources.stringResource(R.string.delete_note)
            val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                parentView.snackbar(resources.stringResource(R.string.note_is_deleted), anchorView = parentAnchorView)
                findNavController().popBackStack(args.destination, false)
                dismiss()
                if (viewModel.note.value.reminderDate != null)
                    alarmManager.cancelAlarm(requireContext(), viewModel.note.value.id)
                viewModel.deleteNote()
            }

            findNavController().navigateSafely(
                NoteDialogFragmentDirections.actionNoteDialogFragmentToConfirmationDialogFragment(
                    confirmationText,
                    descriptionText,
                    btnText,
                    clickListener,
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SelectDirectoryRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
//                val attributes = PrintAttributes.Builder()
//                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                    .setResolution(PrintAttributes.Resolution("Standard", "Standard", 100, 100))
//                    .build()
//                val textView = TextView(requireContext())
//                textView.text = viewModel.note.value.body
//                val layout = LinearLayout(requireContext()).apply {
//                    setPadding(16.dp)
//                    addView(textView)
//                }
//
//                PrintedPdfDocument(requireContext(), attributes).apply {
//                    startPage(0).apply {
//                        layout.measure(canvas.width, canvas.height)
//                        layout.layout(0, 0, canvas.width, canvas.height)
//                        layout.draw(canvas)
//                        finishPage(this)
//                    }
                val documentFile = DocumentFile.fromTreeUri(requireContext(), uri)?.createFile("application/pdf", "Example.pdf")
//                val uri = FileProvider.getUriForFile(requireContext(), requireContext().applicationContext.packageName + ".provider", outputFile)
//                    val outputStream = requireContext().contentResolver.openOutputStream(documentFile!!.uri)
//                    writeTo(outputStream)
//                    close()
//                }

                val outputFile = File(requireContext().externalCacheDir, "example.pdf")
                PdfGenerator.create(outputFile, viewModel.note.value.body, requireContext(), object : PdfGenerator.ResultCallback {
                    override fun onSuccess(file: File) {

                    }

                    override fun onFailure(message: String?) {
                    }
                })

//                val documentUri = requireContext().exportNote(uri, viewModel.library.value, viewModel.note.value)
                val parentView = requireParentFragment().requireView()
//                val parentAnchorView = parentView.findViewById<FloatingActionButton>(R.id.fab)
//                val message = resources.stringResource(R.string.note_is_exported, documentUri?.directoryPath)
//                parentView.snackbar(message, parentAnchorView)
                findNavController().navigateUp()
            }
        }
    }

    private fun NoteDialogFragmentBinding.setupNote(note: Note) {

        if (note.isPinned) {
            tvPinNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_pin_off_24, 0, 0, 0)
            tvPinNote.text = resources.stringResource(R.string.unpin_note)
        } else {
            tvPinNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_pin_24, 0, 0, 0)
            tvPinNote.text = resources.stringResource(R.string.pin_note)
        }

        if (note.isArchived) {
            tvArchiveNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_unarchive_24, 0, 0, 0)
            tvArchiveNote.text = resources.stringResource(R.string.unarchive_note)
        } else {
            tvArchiveNote.text = resources.stringResource(R.string.archive_note)
            tvArchiveNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_archive_24, 0, 0, 0)
        }

        if (note.reminderDate == null) {
            tvRemindMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_notification_add_24, 0, 0, 0)
            tvRemindMe.text = resources.stringResource(R.string.add_note_reminder)
        } else {
            tvRemindMe.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_edit_notifications_24, 0, 0, 0)
            tvRemindMe.text = resources.stringResource(R.string.edit_note_reminder)
        }
    }

    private fun NoteDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        baseDialogFragment.tvDialogTitle.setTextColor(resources.colorResource(library.color.toResource()))
        baseDialogFragment.vHead.backgroundTintList = resources.colorStateResource(library.color.toResource())

        listOf(
            tvCopyToClipboard, tvCopyNote, tvOpenInReadingMode, tvShareNote, tvArchiveNote,
            tvDuplicateNote, tvPinNote, tvRemindMe, tvDeleteNote, tvMoveNote, tvExportNote,
        ).forEach { tv -> TextViewCompat.setCompoundDrawableTintList(tv, resources.colorStateResource(library.color.toResource())) }
    }

}