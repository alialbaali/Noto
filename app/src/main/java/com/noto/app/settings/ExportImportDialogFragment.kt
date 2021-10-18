package com.noto.app.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ExportImportDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ZipMimeType = "application/zip"
private const val ZipFileName = "Noto"

class ExportImportDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            exportData(uri)
        } else {
            dismiss()
            requireParentFragment().requireView().snackbar(resources.stringResource(R.string.no_folder_is_selected))
        }
    }

    private val importLauncher = registerForActivityResult(OpenZipDocument()) { uri ->
        if (uri != null) {
            importData(uri)
        } else {
            dismiss()
            requireParentFragment().requireView().snackbar(resources.stringResource(R.string.no_file_is_selected))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ExportImportDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupListeners()
    }

    private fun ExportImportDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply { tvDialogTitle.text = resources.stringResource(R.string.export_import_data) }

    private fun ExportImportDialogFragmentBinding.setupListeners() {
        tvExport.setOnClickListener {
            exportLauncher.launch(Uri.EMPTY)
        }

        tvImport.setOnClickListener {
            importLauncher.launch(emptyArray())
        }
    }

    private fun exportData(uri: Uri) {
        findNavController().navigateSafely(
            ExportImportDialogFragmentDirections.actionExportImportDialogFragmentToProgressDialogFragment(
                resources.stringResource(R.string.exporting_data)
            )
        )
        val zipFile = DocumentFile.fromTreeUri(requireContext(), uri)?.createFile(ZipMimeType, ZipFileName)
        if (zipFile != null) {
            val zipFileOutputStream = requireContext().contentResolver?.openOutputStream(zipFile.uri)
            if (zipFileOutputStream != null) {
                lifecycleScope.launchWhenResumed {
                    withContext(Dispatchers.IO) {
                        val exportedData = viewModel.exportData()
                        writeDataToZipFile(zipFileOutputStream, exportedData)
                    }
                }.invokeOnCompletion {
                    requireParentFragment().requireView().snackbar(resources.stringResource(R.string.data_is_exported, zipFile.uri.directoryPath))
                    findNavController().navigateUp()
                    dismiss()
                }
            } else {
                requireParentFragment().requireView().snackbar(resources.stringResource(R.string.exporting_failed))
                findNavController().navigateUp()
                dismiss()
            }
        } else {
            requireParentFragment().requireView().snackbar(resources.stringResource(R.string.create_file_failed))
            findNavController().navigateUp()
            dismiss()
        }
    }

    private fun importData(uri: Uri) {
        findNavController().navigateSafely(
            ExportImportDialogFragmentDirections.actionExportImportDialogFragmentToProgressDialogFragment(
                resources.stringResource(R.string.importing_data)
            )
        )
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        if (inputStream != null) {
            lifecycleScope.launchWhenResumed {
                withContext(Dispatchers.IO) {
                    val data = readDataFromZipFile(inputStream)
                    viewModel.importData(data)
                }
            }.invokeOnCompletion {
                requireParentFragment().requireView().snackbar(resources.stringResource(R.string.data_is_imported))
                findNavController().navigateUp()
                dismiss()
            }
        } else {
            requireParentFragment().requireView().snackbar(resources.stringResource(R.string.importing_failed))
            findNavController().navigateUp()
            dismiss()
        }
    }
}