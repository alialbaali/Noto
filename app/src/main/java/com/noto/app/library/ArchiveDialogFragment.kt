package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.ArchiveDialogFragmentBinding
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.note.NoteViewModel
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArchiveDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel>()

    private val args by navArgs<ArchiveDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ArchiveDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.note_options)
        }

        viewModel.getNoteById(args.notoId)

        tvUnarchiveNoto.setOnClickListener {
            viewModel.setNotoArchived(false)
            viewModel.updateNote()
            dismiss()
        }

    }

}