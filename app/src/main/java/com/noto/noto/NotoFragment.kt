package com.noto.noto

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.R
import com.noto.databinding.FragmentNotoBinding
import com.noto.library.LibraryListViewModel
import com.noto.util.getValue
import org.koin.android.viewmodel.ext.android.viewModel

class NotoFragment : Fragment() {

    // Binding
    private val binding by lazy {
        FragmentNotoBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val viewModel by viewModel<NotoViewModel>()

    private val args by navArgs<NotoFragmentArgs>()

    private val imm by lazy { requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private val libraryListViewModel by viewModel<LibraryListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        imm.showSoftInput(binding.etNotoBody, InputMethodManager.SHOW_IMPLICIT)

        viewModel.getNoto(args.libraryId, args.notoId)
        viewModel.getLibraryById(args.libraryId)
        viewModel.getLabels()

//        viewModel.getNotoById(args.libraryId, args.notoId)

        binding.tb.let { tb ->

            tb.setNavigationOnClickListener {
                imm.hideSoftInputFromWindow(binding.etNotoBody.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                this.findNavController().navigateUp()
                viewModel.saveNoto(args.notoId)
            }
        }

        binding.tb.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_noto -> {
                    viewModel.deleteNoto()
                    this.findNavController().navigateUp()
                    binding.etNotoBody.clearFocus()
                }
//                R.id.library -> {
//                    LibrarySelectorDialog(requireContext(), libraryListViewModel, viewModel, args.notoId)
//                }
//                R.id.schedule -> ScheduleDialog(requireContext())
            }
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this@NotoFragment.findNavController().navigateUp()
//            viewModel.saveNoto(args.libraryId, args.notoId)
            viewModel.saveNoto(args.notoId)
        }.isEnabled = true


        viewModel.library.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.tb.title = it.libraryTitle
                binding.tb.setBackgroundResource(it.notoColor.getValue())
            }
        })
//        binding.etNotoBody.requestFocus()
//        imm.showSoftInput(binding.etNotoBody, InputMethodManager.SHOW_FORCED)
//
//        binding.tv.setOnClickListener {
//            LibrarySelectorDialog(requireContext(), libraryListViewModel, viewModel, args.notoId)
//        }

//        viewModel.notoLabels.observe(viewLifecycleOwner, Observer { labels ->
//
//            Timber.i("CHANGED")
//
//            for (label in labels) {
//
//                Chip(requireContext()).also { chip ->
//
//                    chip.id = label.labelId.toInt()
//
//                    chip.chipBackgroundColor = resources.getColorStateList(label.notoColor.getValue())
//
//                    chip.text = label.labelTitle
//
//                    chip.gravity = Gravity.CENTER
//
//                    chip.setTextColor(resources.getColor(R.color.colorPrimary))
//
//                    chip.closeIconTint = resources.getColorStateList(R.color.colorOnPrimary)
//
//                    chip.isCheckable = false
//
//                    chip.isCloseIconVisible = false
//
//                    chip.setOnClickListener {
//                        chip.isCloseIconVisible = !chip.isCloseIconVisible
//                    }
//
//                    chip.setOnCloseIconClickListener {
//                        viewModel.notoLabels.value!!.remove(label)
//                    }
//
//                    binding.cgLabels.addView(chip)
//                }
//            }
//
//        })

//        binding.cpAdd.setOnClickListener {
//            LabelSelectorDialog(requireContext(), viewModel)
//        }

        return binding.root
    }
}
