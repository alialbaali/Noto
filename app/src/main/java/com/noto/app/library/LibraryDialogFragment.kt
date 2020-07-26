package com.noto.app.library

import android.annotation.SuppressLint
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
import com.noto.app.databinding.FragmentDialogLibraryBinding
import com.noto.app.util.getValue
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LibraryDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogLibraryBinding

    private val viewModel by sharedViewModel<LibraryViewModel>()

    private val args by navArgs<LibraryDialogFragmentArgs>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogLibraryBinding.inflate(inflater, container, false)

        viewModel.getLibrary(args.libraryId)

        viewModel.library.observe(viewLifecycleOwner, Observer { library ->
            library?.let {

                binding.vHead.backgroundTintList = ResourcesCompat.getColorStateList(resources, it.notoColor.getValue(), null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    listOf(binding.tvEditLibrary).forEach { tv ->
                        tv.compoundDrawableTintList = ResourcesCompat.getColorStateList(resources, it.notoColor.getValue(), null)
                    }
                }

            }
        })

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
                }
            }.show(parentFragmentManager, null)
        }

        return binding.root
    }

}