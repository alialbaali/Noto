package com.noto.app.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ExportImportDialogFragmentBinding
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val FileType = "application/json"
private const val FileName = "Noto"

class ExportImportDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            exportJson(uri)
        } else {
            context?.let { context ->
                parentFragment?.view?.snackbar(context.stringResource(R.string.no_folder_is_selected))
            }
            dismiss()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            importJson(uri)
        } else {
            context?.let { context ->
                parentFragment?.view?.snackbar(context.stringResource(R.string.no_file_is_selected))
            }
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ExportImportDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupListeners()
    }

    private fun ExportImportDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.export_import_data)
            }
        }

    private fun ExportImportDialogFragmentBinding.setupListeners() {
        tvExport.setOnClickListener {
            exportLauncher.launch(Uri.EMPTY)
        }

        tvImport.setOnClickListener {
            importLauncher.launch(arrayOf(FileType))
        }
    }

    private fun exportJson(uri: Uri) {
        context?.let { context ->
            navController?.navigateSafely(
                ExportImportDialogFragmentDirections.actionExportImportDialogFragmentToProgressIndicatorDialogFragment(
                    context.stringResource(R.string.exporting_data)
                )
            )
            val file = DocumentFile.fromTreeUri(context, uri)?.createFile(FileType, FileName)
            if (file != null) {
                val fileOutputStream = context.contentResolver?.openOutputStream(file.uri)
                if (fileOutputStream != null) {
                    lifecycleScope.launchWhenCreated {
                        val json = viewModel.exportJson()
                        writeTextToOutputStream(fileOutputStream, json)
                    }.invokeOnCompletion {
                        parentFragment?.view?.snackbar(context.stringResource(R.string.data_is_exported, file.uri.directoryPath))
                        navController?.navigateUp()
                        dismiss()
                    }
                } else {
                    parentFragment?.view?.snackbar(context.stringResource(R.string.exporting_failed))
                    navController?.navigateUp()
                    dismiss()
                }
            } else {
                parentFragment?.view?.snackbar(context.stringResource(R.string.create_file_failed))
                navController?.navigateUp()
                dismiss()
            }
        }
    }

    private fun importJson(uri: Uri) {
        context?.let { context ->
            navController?.navigateSafely(
                ExportImportDialogFragmentDirections.actionExportImportDialogFragmentToProgressIndicatorDialogFragment(
                    context.stringResource(R.string.importing_data)
                )
            )
            val inputStream = context.contentResolver?.openInputStream(uri)
            if (inputStream != null) {
                lifecycleScope.launchWhenCreated {
                    val json = readTextFromInputStream(inputStream)
                    viewModel.importJson(json)
                }.invokeOnCompletion {
                    parentFragment?.view?.snackbar(context.stringResource(R.string.data_is_imported))
                    navController?.navigateUp()
                    dismiss()
                }
            } else {
                parentFragment?.view?.snackbar(context.stringResource(R.string.importing_failed))
                navController?.navigateUp()
                dismiss()
            }
        }
    }
}