package com.noto.app.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NewLibraryDialogFragmentArgs>()

    private val imm by lazy { requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NewLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupState(baseDialogFragment)
            setupListeners()
        }

    private fun NewLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        if (args.libraryId == 0L) {
            tvDialogTitle.text = resources.stringResource(R.string.new_library)
        } else {
            tvDialogTitle.text = resources.stringResource(R.string.edit_library)
            btnCreate.text = resources.stringResource(R.string.done)
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv.clipToOutline = true
        et.requestFocus()
        requireActivity().showKeyboard(root)

        viewModel.state
            .onEach { state -> setupLibrary(state.library, baseDialogFragment) }
            .launchIn(lifecycleScope)

        viewModel.notoColors
            .onEach { pairs -> setupNotoColors(pairs) }
            .launchIn(lifecycleScope)
    }

    private fun NewLibraryDialogFragmentBinding.setupListeners() {
        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.error = resources.stringResource(R.string.empty_title)
            } else {
                requireActivity().hideKeyboard(root)
                dismiss()
                updatePinnedShortcut(title)
                viewModel.createOrUpdateLibrary(title)
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        et.setText(library.title)
        et.setSelection(library.title.length)
        rv.smoothScrollToPosition(library.color.ordinal)
        val color = resources.colorResource(library.color.toResource())
        if (library.id != 0L) {
            baseDialogFragment.tvDialogTitle.setTextColor(color)
            baseDialogFragment.vHead.background?.mutate()?.setTint(color)
        }
    }

    private fun NewLibraryDialogFragmentBinding.setupNotoColors(pairs: List<Pair<NotoColor, Boolean>>) {
        rv.withModels {
            pairs.forEach { pair ->
                notoColorItem {
                    id(pair.first.ordinal)
                    notoColor(pair.first)
                    isChecked(pair.second)
                    onClickListener { _ ->
                        viewModel.selectNotoColor(pair.first)
                    }
                }
            }
        }
    }

    private fun NewLibraryDialogFragmentBinding.updatePinnedShortcut(title: String) {
        val library = viewModel.state.value.library.copy(
            title = title,
            color = viewModel.notoColors.value.first { it.second }.first
        )
        ShortcutManagerCompat.updateShortcuts(requireContext(), listOf(requireContext().createPinnedShortcut(library)))
    }
}