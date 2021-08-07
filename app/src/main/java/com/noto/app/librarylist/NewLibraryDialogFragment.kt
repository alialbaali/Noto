package com.noto.app.librarylist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.notelist.NoteListViewModel
import com.noto.app.util.hideKeyboard
import com.noto.app.util.showKeyboard
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private val listener = NotoColorListAdapter.NotoColorClickListener {
        viewModel.selectNotoColor(it)
    }

    private val adapter = NotoColorListAdapter(listener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NewLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
            setupRV()
        }

    private fun NewLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        if (args.libraryId == 0L) {
            tvDialogTitle.text = resources.stringResource(R.string.new_library)
        } else {
            tvDialogTitle.text = resources.stringResource(R.string.edit_library)
            btnCreate.text = resources.stringResource(R.string.done)
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupRV() {
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun NewLibraryDialogFragmentBinding.setupState() {
        et.requestFocus()
        imm.showKeyboard()

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { notoColors -> adapter.submitList(notoColors) }
            .launchIn(lifecycleScope)
    }

    private fun NewLibraryDialogFragmentBinding.setupListeners() {
        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.error = resources.stringResource(R.string.empty_title)
            } else {
                imm.hideKeyboard(et.windowToken)
                dismiss()
                viewModel.createOrUpdateLibrary(title)
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupLibrary(library: Library) {
        et.setText(library.title)
        et.setSelection(library.title.length)
        rv.smoothScrollToPosition(library.color.ordinal)
    }

}