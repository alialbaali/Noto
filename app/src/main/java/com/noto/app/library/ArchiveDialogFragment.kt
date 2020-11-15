package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.databinding.FragmentDialogArchiveBinding
import com.noto.app.note.NoteViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ArchiveDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogArchiveBinding

    private val viewModel by viewModel<NoteViewModel>()

    private val args by navArgs<ArchiveDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogArchiveBinding.inflate(inflater, container, false)

        viewModel.getNoteById(args.notoId)

        binding.tvUnarchiveNoto.setOnClickListener {
            viewModel.setNotoArchived(false)
            viewModel.updateNote()
            dismiss()
        }

        return binding.root
    }

}