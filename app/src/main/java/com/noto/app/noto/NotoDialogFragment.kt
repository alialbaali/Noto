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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogNotoBinding
import com.noto.app.util.colorStateResource
import com.noto.app.util.drawableResource
import com.noto.app.util.toResource
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

        viewModel.library.observe(viewLifecycleOwner, Observer { library ->
            library?.let {

                binding.vHead.backgroundTintList = colorStateResource(it.notoColor.toResource())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    listOf(binding.tvCopyToClipboard, binding.tvShareNoto, binding.tvArchiveNoto, binding.tvRemindMe).forEach { tv ->
                        tv.compoundDrawableTintList = colorStateResource(it.notoColor.toResource())
                    }
                }

            }

        })

        viewModel.noto.observe(viewLifecycleOwner, Observer { noto ->
            noto?.let {

                binding.tvArchiveNoto.compoundDrawablesRelative[0] =
                    if (noto.notoIsArchived) drawableResource(R.drawable.ic_outline_unarchive_24)
                    else drawableResource(R.drawable.archive_arrow_down_outline)

                binding.tvRemindMe.compoundDrawablesRelative[0] =
                    if (noto.notoReminder == null) drawableResource(R.drawable.bell_plus_outline)
                    else drawableResource(R.drawable.bell_ring_outline)

            }

        })

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
            dismiss()

            ConfirmationDialogFragment { dialogFragment, dialogBinding ->

                dialogBinding.btnConfirm.text = dialogFragment.getString(R.string.delete_noto)
                dialogBinding.tvTitle.text = dialogFragment.getString(R.string.delete_noto_confirmation)

                dialogBinding.btnConfirm.setOnClickListener {
                    dialogFragment.dismiss()
                    viewModel.deleteNoto()
                }
            }.show(parentFragmentManager, null)
        }


        return binding.root
    }

}