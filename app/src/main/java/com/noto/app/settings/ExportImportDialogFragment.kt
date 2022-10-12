package com.noto.app.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.ExportImportDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val JsonFileType = "application/json"
private const val OctetStreamFileType = "application/octet-stream"
private val FileTypes = arrayOf(JsonFileType, OctetStreamFileType)
private const val FileName = "Noto.json"

class ExportImportDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val parentView by lazy { parentFragment?.view }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            exportJson(uri)
        } else {
            context?.let { context ->
                parentView?.snackbar(context.stringResource(R.string.no_folder_is_selected), R.drawable.ic_round_warning_24)
            }
            dismiss()
        }
    }

    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            importJson(uri)
        } else {
            context?.let { context ->
                parentView?.snackbar(context.stringResource(R.string.no_file_is_selected), R.drawable.ic_round_warning_24)
            }
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ExportImportDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun ExportImportDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.export_import_data)

        viewModel.isImportFinished
            .onEach {
                context?.let { context ->
                    parentView?.snackbar(context.stringResource(R.string.data_is_imported), R.drawable.ic_round_file_download_24)
                }
                navController?.navigateUp()
                dismiss()
            }
            .launchIn(lifecycleScope)
    }

    private fun ExportImportDialogFragmentBinding.setupListeners() {
        tvExport.setOnClickListener {
            exportLauncher.launch(Uri.EMPTY)
        }

        tvImport.setOnClickListener {
            importLauncher.launch(FileTypes)
        }
    }

    private fun exportJson(uri: Uri) {
        context?.let { context ->
            navController?.navigateSafely(
                ExportImportDialogFragmentDirections.actionExportImportDialogFragmentToProgressIndicatorDialogFragment(
                    context.stringResource(R.string.exporting_data)
                )
            )
            val file = DocumentFile.fromTreeUri(context, uri)?.createFile(JsonFileType, FileName)
            if (file != null) {
                val fileOutputStream = context.contentResolver?.openOutputStream(file.uri)
                if (fileOutputStream != null) {
                    lifecycleScope.launchWhenCreated {
                        val json = viewModel.exportJson()
                        writeTextToOutputStream(fileOutputStream, json)
                    }.invokeOnCompletion {
                        parentView?.snackbar(
                            context.stringResource(R.string.data_is_exported, file.uri.directoryPath),
                            R.drawable.ic_round_file_upload_24,
                        )
                        navController?.navigateUp()
                        dismiss()
                    }
                } else {
                    parentView?.snackbar(context.stringResource(R.string.exporting_failed), R.drawable.ic_round_error_24)
                    navController?.navigateUp()
                    dismiss()
                }
            } else {
                parentView?.snackbar(context.stringResource(R.string.create_file_failed), R.drawable.ic_round_error_24)
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
                }.invokeOnCompletion { viewModel.emitIsImportFinished() }
            } else {
                parentView?.snackbar(context.stringResource(R.string.importing_failed), R.drawable.ic_round_error_24)
                navController?.navigateUp()
                dismiss()
            }
        }
    }
}