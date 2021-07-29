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
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class NoteDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteDialogFragmentArgs>()

    private val clipboardManager by lazy { requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialog = BaseDialogFragmentBinding.bind(root)

        baseDialog.tvDialogTitle.text = stringResource(R.string.note_options)

        viewModel.library
            .onEach {
                baseDialog.tvDialogTitle.setTextColor(colorResource(it.color.toResource()))
                baseDialog.vHead.backgroundTintList = colorStateResource(it.color.toResource())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    listOf(tvCopyToClipboard, tvShareNoto, tvArchiveNoto, tvRemindMe)
                        .forEach { tv -> tv.compoundDrawableTintList = colorStateResource(it.color.toResource()) }
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach {
                tvArchiveNoto.compoundDrawablesRelative[0] =
                    if (it.isArchived) drawableResource(R.drawable.ic_outline_unarchive_24)
                    else drawableResource(R.drawable.archive_arrow_down_outline)

                tvRemindMe.compoundDrawablesRelative[0] =
                    if (it.reminderDate == null) drawableResource(R.drawable.bell_plus_outline)
                    else drawableResource(R.drawable.bell_ring_outline)
            }
            .launchIn(lifecycleScope)


        tvArchiveNoto.setOnClickListener {
            dismiss()
            if (viewModel.note.value.isArchived) viewModel.setNotoArchived(false) else viewModel.setNotoArchived(true)
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

            val title = getString(R.string.delete_note_confirmation)
            val btnText = getString(R.string.delete_note)
            val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                viewModel.deleteNoto()
            }

            findNavController().navigate(NoteDialogFragmentDirections.actionNotoDialogFragmentToConfirmationDialogFragment(title, null, btnText, clickListener))
        }

    }
}