package com.noto.app.librarylist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.notelist.NoteListViewModel
import com.noto.app.util.hideKeyboard
import com.noto.app.util.showKeyboard
import com.noto.app.util.stringResource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLibraryDialogFragment : BaseDialogFragment() {

    private lateinit var binding: NewLibraryDialogFragmentBinding

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private val adapter by lazy { NotoColorListAdapter(listener) }

    private val listener by lazy {
        NotoColorListAdapter.NotoColorClickListener {
            viewModel.selectNotoColor(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = NewLibraryDialogFragmentBinding.inflate(inflater, container, false)

        BaseDialogFragmentBinding.bind(binding.root).apply {
            if (args.libraryId == 0L) {
                tvDialogTitle.text = resources.stringResource(R.string.new_library)
            } else {
                tvDialogTitle.text = resources.stringResource(R.string.edit_library)
                binding.btnCreate.text = resources.stringResource(R.string.done)
            }
        }

        binding.et.requestFocus()
        imm.showKeyboard()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) { imm.hideKeyboard(binding.et.windowToken) }
            .isEnabled = true

        collectState()
        setupListeners()
        setupRV()

        return binding.root
    }

    private fun setupRV() {
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun collectState() {
        viewModel.library
            .onEach {
                binding.et.setText(it.title)
                binding.et.setSelection(it.title.length)
                binding.rv.smoothScrollToPosition(it.color.ordinal)
            }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { adapter.submitList(it) }
            .launchIn(lifecycleScope)
    }

    private fun setupListeners() {
        binding.btnCreate.setOnClickListener {
            val title = binding.et.text.toString()
            if (title.isBlank()) {
                binding.til.error = resources.stringResource(R.string.empty_title)
            } else {
                imm.hideKeyboard(binding.et.windowToken)
                dismiss()
                viewModel.createOrUpdateLibrary(title)
            }
        }
    }
}