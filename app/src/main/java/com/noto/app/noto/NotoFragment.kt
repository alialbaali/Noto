package com.noto.app.noto

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
import com.noto.app.library.LibraryListViewModel
import com.noto.app.noto.NotoFragmentArgs
import com.noto.app.util.getValue
import org.koin.android.viewmodel.ext.android.viewModel

class NotoFragment : Fragment() {

    private val binding by lazy {
        FragmentNotoBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }

    private val viewModel by viewModel<NotoViewModel>()

    private val args by navArgs<NotoFragmentArgs>()

    private val imm by lazy { requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        imm.showSoftInput(binding.etNotoBody, InputMethodManager.SHOW_IMPLICIT)

        viewModel.getNoto(args.libraryId, args.notoId)
        viewModel.getLibraryById(args.libraryId)

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
            }
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this@NotoFragment.findNavController().navigateUp()
            viewModel.saveNoto(args.notoId)
        }.isEnabled = true


        viewModel.library.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.tb.title = it.libraryTitle
                binding.tb.setBackgroundResource(it.notoColor.getValue())
            }
        })

        return binding.root
    }
}
