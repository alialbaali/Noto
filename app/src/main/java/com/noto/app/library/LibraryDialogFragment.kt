package com.noto.app.library

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryDialogFragmentBinding
import com.noto.app.util.colorStateResource
import com.noto.app.util.stringResource
import com.noto.app.util.toResource
import com.noto.app.util.withBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<LibraryDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.library_options)
        }

        viewModel.getLibrary(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner) { library ->
            library?.let {

                baseDialog.vHead.backgroundTintList = colorStateResource(it.color.toResource())
                baseDialog.tvDialogTitle.setTextColor(colorStateResource(it.color.toResource()))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    listOf(tvEditLibrary, tvSorting).forEach { tv ->
                        tv.compoundDrawableTintList = colorStateResource(it.color.toResource())
                    }
                }

            }
        }

        tvEditLibrary.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToNewLibraryDialogFragment(args.libraryId))
        }

        tvDeleteLibrary.setOnClickListener {
            dismiss()

            ConfirmationDialogFragment { dialogFragment, dialogBinding ->

                dialogBinding.btnConfirm.text = dialogFragment.getString(R.string.delete_library)
                dialogBinding.tvTitle.text = dialogFragment.getString(R.string.delete_library_confirmation)

                dialogBinding.btnConfirm.setOnClickListener {
                    dialogFragment.dismiss()
                    viewModel.deleteLibrary()
                    dialogFragment.findNavController().navigate(R.id.libraryListFragment)
                }
            }.show(parentFragmentManager, null)
        }

        tvSorting.setOnClickListener {
            dismiss()

            findNavController()
                .navigate(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToSortingDialogFragment())
        }
    }
}