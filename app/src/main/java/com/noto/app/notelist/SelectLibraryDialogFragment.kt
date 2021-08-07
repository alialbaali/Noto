package com.noto.app.notelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SelectLibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.librarylist.LibraryListAdapter
import com.noto.app.librarylist.LibraryListViewModel
import com.noto.app.util.LayoutManager
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class SelectLibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryListViewModel>()

    private val args by navArgs<SelectLibraryDialogFragmentArgs>()

    private val libraryItemClickListener by lazy {
        object : LibraryListAdapter.LibraryItemClickListener {
            override fun onClick(library: Library) {
                args.selectLibraryItemClickListener.onClick(library.id)
                dismiss()
            }

            override fun onLongClick(library: Library) {

            }

            override fun countLibraryNotes(library: Library): Int = viewModel.countNotes(library.id)
        }
    }

    private val adapter = LibraryListAdapter(libraryItemClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SelectLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
        }

    private fun SelectLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.select_library)
    }

    private fun SelectLibraryDialogFragmentBinding.setupState() {
        rv.adapter = adapter

        viewModel.libraries
            .map { libraries -> libraries.filter { library -> library.id != args.libraryId } }
            .onEach { libraries -> setupLibraries(libraries) }
            .launchIn(lifecycleScope)

        viewModel.layoutManager
            .onEach { layoutManager -> setupLayoutManager(layoutManager) }
            .launchIn(lifecycleScope)
    }

    private fun SelectLibraryDialogFragmentBinding.setupLibraries(libraries: List<Library>) {
        if (libraries.isEmpty()) {
            tvPlaceHolder.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            tvPlaceHolder.visibility = View.GONE
            rv.visibility = View.VISIBLE
            adapter.submitList(libraries)
        }
    }

    private fun SelectLibraryDialogFragmentBinding.setupLayoutManager(layoutManager: LayoutManager) {
        when (layoutManager) {
            LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LayoutManager.Grid -> rv.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    fun interface SelectLibraryItemClickListener : Serializable {
        fun onClick(libraryId: Long)
    }
}