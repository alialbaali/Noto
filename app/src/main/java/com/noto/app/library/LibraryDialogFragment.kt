package com.noto.app.library

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.LibraryDialogFragmentBinding
import com.noto.app.util.colorStateResource
import com.noto.app.util.toResource
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LibraryDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: LibraryDialogFragmentBinding

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<LibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = LibraryDialogFragmentBinding.inflate(inflater, container, false)

        viewModel.getLibrary(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner) { library ->
            library?.let {

                binding.vHead.backgroundTintList = colorStateResource(it.color.toResource())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    listOf(binding.tvEditLibrary).forEach { tv ->
                        tv.compoundDrawableTintList = colorStateResource(it.color.toResource())
                    }
                }

            }
        }

        binding.tvEditLibrary.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToNewLibraryDialogFragment(args.libraryId))
        }

        binding.tvDeleteLibrary.setOnClickListener {
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

        return binding.root
    }

}