package com.noto.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.noto.BaseBottomSheetDialogFragment
import com.noto.R
import com.noto.databinding.FragmentDialogArchiveBinding
import com.noto.noto.NotoViewModel
import com.noto.util.snackbar
import com.noto.util.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ArchiveDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogArchiveBinding

    private val viewModel by viewModel<NotoViewModel>()

    private val args by navArgs<ArchiveDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogArchiveBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ArchiveDialogFragment
        }

        viewModel.getNotoById(args.notoId)

        binding.tvUnarchiveNoto.setOnClickListener {
            viewModel.setArchived(false)
            viewModel.updateNoto()
            dismiss()
        }

        return binding.root
    }

}