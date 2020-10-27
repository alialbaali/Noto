package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogArchiveBinding
import com.noto.app.library.ArchiveDialogFragmentArgs
import com.noto.app.noto.NotoViewModel
import org.koin.android.viewmodel.ext.android.viewModel

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