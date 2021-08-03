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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable

class SelectLibraryDialogFragment : BaseDialogFragment() {

    private lateinit var binding: SelectLibraryDialogFragmentBinding

    private val adapter by lazy { LibraryListAdapter(libraryItemClickListener) }

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

    private val viewModel by viewModel<LibraryListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SelectLibraryDialogFragmentBinding.inflate(inflater, container, false)

        BaseDialogFragmentBinding.bind(binding.root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.select_library)
        }

        binding.rv.adapter = adapter

        viewModel.libraries
            .map { it.filter { it.id != args.libraryId } }
            .onEach {
                if (it.isEmpty()) {
                    binding.tvPlaceHolder.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                } else {
                    binding.tvPlaceHolder.visibility = View.GONE
                    binding.rv.visibility = View.VISIBLE
                    adapter.submitList(it)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.layoutManager
            .onEach {
                when (it) {
                    LayoutManager.Linear -> binding.rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    LayoutManager.Grid -> binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
                }
            }
            .launchIn(lifecycleScope)

        return binding.root
    }

    fun interface SelectLibraryItemClickListener : Serializable {
        fun onClick(libraryId: Long)
    }

}