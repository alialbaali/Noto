package com.noto.app.noto

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogNotoBinding
import com.noto.app.noto.NotoDialogFragmentArgs
import com.noto.app.noto.NotoDialogFragmentDirections
import com.noto.app.util.getValue
import com.noto.app.util.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel

class NotoDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogNotoBinding

    private val viewModel by sharedViewModel<NotoViewModel>()

    private val args by navArgs<NotoDialogFragmentArgs>()

    private val clipboardManager by lazy { requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogNotoBinding.inflate(inflater, container, false)

        if (args.notoId != 0L) viewModel.getNotoById(args.notoId)

        viewModel.getLibraryById(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner) {
            binding.vHead.backgroundTintList = ResourcesCompat.getColorStateList(resources, it.notoColor.getValue(), null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                listOf(binding.tvCopyToClipboard, binding.tvShareNoto, binding.tvArchiveNoto, binding.tvRemindMe).forEach { tv ->
                    tv.compoundDrawableTintList = ResourcesCompat.getColorStateList(resources, it.notoColor.getValue(), null)
                }
            }
        }

        viewModel.noto.observe(viewLifecycleOwner) { noto ->

            binding.tvArchiveNoto.compoundDrawablesRelative[0] =
                if (noto.notoIsArchived) ResourcesCompat.getDrawable(resources, R.drawable.ic_outline_unarchive_24, null)
                else ResourcesCompat.getDrawable(resources, R.drawable.archive_arrow_down_outline, null)

            binding.tvRemindMe.compoundDrawablesRelative[0] =
                if (noto.notoReminder == null) ResourcesCompat.getDrawable(resources, R.drawable.bell_plus_outline, null)
                else ResourcesCompat.getDrawable(resources, R.drawable.bell_ring_outline, null)

        }

        binding.tvArchiveNoto.setOnClickListener {
            dismiss()
            if (viewModel.noto.value?.notoIsArchived == true) viewModel.setArchived(false) else viewModel.setArchived(true)
            viewModel.updateNoto()
        }

        binding.tvRemindMe.setOnClickListener {
            dismiss()
            findNavController().navigate(NotoDialogFragmentDirections.actionNotoDialogFragmentToReminderDialogFragment(args.notoId))
        }

        binding.tvCopyToClipboard.setOnClickListener { v ->
            dismiss()
            val clipData = ClipData.newPlainText(viewModel.library.value?.libraryTitle, "${viewModel.noto.value?.notoTitle}\n${viewModel.noto.value?.notoBody}")
            clipboardManager.setPrimaryClip(clipData)
            v.toast(getString(R.string.copied_to_clipboard))
        }

        binding.tvShareNoto.setOnClickListener {

            dismiss()

            val noto = viewModel.noto.value

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_MIME_TYPES, noto?.notoTitle)
                putExtra(Intent.EXTRA_MIME_TYPES, noto?.notoBody)
            }

            val chooser = Intent.createChooser(intent, "${getString(R.string.share)} ${noto?.notoTitle} to")

            startActivity(chooser)

        }

        binding.tvDeleteNoto.setOnClickListener {

            val navController = findNavController()

            ConfirmationDialogFragment { dialogFragment, dialogBinding ->

                dialogBinding.btnConfirm.text = dialogFragment.getString(R.string.delete_noto)
                dialogBinding.tvTitle.text = dialogFragment.getString(R.string.delete_noto_confirmation)

                dialogBinding.btnConfirm.setOnClickListener {
                    dialogFragment.dismiss()
                    navController.navigate(NotoDialogFragmentDirections.actionNotoDialogFragmentToLibraryFragment(args.libraryId))
                    viewModel.deleteNoto()
                }
            }.show(parentFragmentManager, null)
        }


        return binding.root
    }

}