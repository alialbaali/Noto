package com.noto.app.notelist

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryDialogFragmentArgs>()

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.library_options)
        }

        setupListeners()
        collectState(baseDialog)
    }

    private fun LibraryDialogFragmentBinding.collectState(baseDialog: BaseDialogFragmentBinding) {
        viewModel.library
            .onEach {
                val resource = resources.colorStateResource(it.color.toResource())
                baseDialog.vHead.backgroundTintList = resource
                baseDialog.tvDialogTitle.setTextColor(resource)
                TextViewCompat.setCompoundDrawableTintList(tvEditLibrary, resource)
                TextViewCompat.setCompoundDrawableTintList(tvDeleteLibrary, resource)
            }
            .launchIn(lifecycleScope)
    }

    private fun LibraryDialogFragmentBinding.setupListeners() {
        tvEditLibrary.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToNewLibraryDialogFragment(args.libraryId))
        }

        tvDeleteLibrary.setOnClickListener {
            val title = resources.stringResource(R.string.delete_library_confirmation)
            val btnText = resources.stringResource(R.string.delete_library)
            val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                val parentView = requireParentFragment().requireView()
                val parentAnchorView = parentView.findViewById<FloatingActionButton>(R.id.fab)
                parentView.snackbar(resources.stringResource(R.string.library_is_deleted), anchorView = parentAnchorView)
                findNavController().popBackStack(R.id.libraryListFragment, false)
                dismiss()
                viewModel.notes.value
                    .filter { it.reminderDate != null }
                    .forEach { alarmManager.cancelAlarm(requireContext(), it.id) }
                viewModel.deleteLibrary()
            }

            findNavController().navigate(
                LibraryDialogFragmentDirections.actionLibraryDialogFragmentToConfirmationDialogFragment(
                    title,
                    null,
                    btnText,
                    clickListener,
                )
            )
        }
    }
}