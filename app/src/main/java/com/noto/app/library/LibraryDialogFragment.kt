package com.noto.app.library

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.ShortcutManagerCompat
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
import com.noto.app.domain.model.Library
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


private const val SelectDirectoryRequestCode = 1

class LibraryDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryDialogFragmentArgs>()

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupListeners()
            setupState(baseDialogFragment)
        }

    private fun LibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.library_options)
    }

    private fun LibraryDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.state
            .onEach { state -> setupLibrary(state.library, baseDialogFragment) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryDialogFragmentBinding.setupListeners() {
        tvExportLibrary.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, SelectDirectoryRequestCode)
        }

        tvEditLibrary.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryDialogFragmentDirections.actionLibraryDialogFragmentToNewLibraryDialogFragment(args.libraryId))
        }

        tvNewNoteShortcut.setOnClickListener {
            dismiss()
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(requireContext()))
                ShortcutManagerCompat.requestPinShortcut(requireContext(), requireContext().createPinnedShortcut(viewModel.state.value.library), null)
        }

        tvDeleteLibrary.setOnClickListener {
            val title = resources.stringResource(R.string.delete_library_confirmation)
            val btnText = resources.stringResource(R.string.delete_library)
            val clickListener = setupConfirmationDialogClickListener()

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

    private fun LibraryDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        val resource = resources.colorStateResource(library.color.toResource())
        baseDialogFragment.vHead.backgroundTintList = resource
        baseDialogFragment.tvDialogTitle.setTextColor(resource)
        listOf(tvEditLibrary, tvNewNoteShortcut, tvExportLibrary, tvDeleteLibrary)
            .forEach { TextViewCompat.setCompoundDrawableTintList(it, resource) }
    }

    private fun setupConfirmationDialogClickListener() = ConfirmationDialogFragment.ConfirmationDialogClickListener {
        val parentView = requireParentFragment().requireView()
        val parentAnchorView = parentView.findViewById<FloatingActionButton>(R.id.fab)
        parentView.snackbar(resources.stringResource(R.string.library_is_deleted), anchorView = parentAnchorView)

        findNavController().popBackStack(R.id.mainFragment, false)
        dismiss()

        viewModel.state.value.notes
            .filter { note -> note.reminderDate != null }
            .forEach { note -> alarmManager.cancelAlarm(requireContext(), note.id) }

        viewModel.deleteLibrary()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SelectDirectoryRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                var documentUri: Uri? = Uri.EMPTY
                viewModel.state.value.notes.forEach { note ->
                    documentUri = requireContext().exportNote(uri, viewModel.state.value.library, note)
                }
                val parentView = requireParentFragment().requireView()
                val parentAnchorView = parentView.findViewById<FloatingActionButton>(R.id.fab)
                val message = resources.stringResource(R.string.library_is_exported) + " ${documentUri?.directoryPath}."
                parentView.snackbar(message, parentAnchorView)
                findNavController().navigateUp()
            }
        }
    }
}