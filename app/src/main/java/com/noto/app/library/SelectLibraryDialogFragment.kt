package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SelectLibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.main.MainViewModel
import com.noto.app.main.libraryItem
import com.noto.app.util.LayoutManager
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class SelectLibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    private val args by navArgs<SelectLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SelectLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
        }

    private fun SelectLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.select_library)
    }

    private fun SelectLibraryDialogFragmentBinding.setupState() {
        viewModel.state
            .map { state -> state.copy(libraries = state.libraries.filter { library -> library.id != args.libraryId }) }
            .onEach { state ->
                setupLayoutManager(state.layoutManager)
                setupLibraries(state.libraries)
            }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectLibraryDialogFragmentBinding.setupLibraries(libraries: List<Library>) {
        if (libraries.isEmpty()) {
            tvPlaceHolder.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            tvPlaceHolder.visibility = View.GONE
            rv.visibility = View.VISIBLE
            rv.withModels {
                libraries.forEach { library ->
                    libraryItem {
                        id(library.id)
                        library(library)
                        notesCount(viewModel.countNotes(library.id))
                        isManualSorting(false)
                        onClickListener { _ ->
                            args.selectLibraryItemClickListener.onClick(library.id)
                            dismiss()
                        }
                        onLongClickListener { _ -> false }
                        onDragHandleTouchListener { _, _ -> false }
                    }
                }
            }
        }
    }

    private fun SelectLibraryDialogFragmentBinding.setupLayoutManager(layoutManager: LayoutManager) {
        when (layoutManager) {
            LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    fun interface SelectLibraryItemClickListener : Serializable {
        fun onClick(libraryId: Long)
    }
}