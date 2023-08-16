package com.noto.app.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.BottomSheetDialogItem
import com.noto.app.theme.NotoTheme
import com.noto.app.util.*
import kotlinx.coroutines.launch
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
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val isImportFinished by viewModel.isImportFinished.collectAsState(initial = null)
                val dataIsImportedText = stringResource(id = R.string.data_is_imported)
                BottomSheetDialog(title = stringResource(id = R.string.export_import_data)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.export_data),
                            onClick = { exportLauncher.launch(Uri.EMPTY) },
                            painter = painterResource(id = R.drawable.ic_round_file_upload_24),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.import_data),
                            onClick = { importLauncher.launch(FileTypes) },
                            painter = painterResource(id = R.drawable.ic_round_file_download_24),
                        )
                    }
                }
                SideEffect {
                    if (isImportFinished != null) {
                        parentView?.snackbar(dataIsImportedText, R.drawable.ic_round_file_download_24)
                        navController?.navigateUp()
                        dismiss()
                    }
                }
            }
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
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            launch {
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
                        }
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
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        launch {
                            val json = readTextFromInputStream(inputStream)
                            viewModel.importJson(json)
                        }.invokeOnCompletion { viewModel.emitIsImportFinished() }
                    }
                }
            } else {
                parentView?.snackbar(context.stringResource(R.string.importing_failed), R.drawable.ic_round_error_24)
                navController?.navigateUp()
                dismiss()
            }
        }
    }
}