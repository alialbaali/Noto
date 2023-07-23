package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.Grouping
import com.noto.app.toColor
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.toResource
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListGroupingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListGroupingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        val navController = navController
        val savedStateHandle = navController?.previousBackStackEntry?.savedStateHandle

        ComposeView(context).apply {
            if (navController == null || savedStateHandle == null) return@apply

            setContent {
                val folder by viewModel.folder.collectAsState()
                val types = Grouping.entries
                val groupingType by savedStateHandle.getStateFlow<Grouping?>(key = Constants.GroupingType, initialValue = null)
                    .collectAsState()

                BottomSheetDialog(title = stringResource(R.string.grouping), headerColor = folder.color.toColor()) {
                    types.forEach { type ->
                        SelectableDialogItem(
                            selected = type == (groupingType ?: folder.grouping),
                            onClick = { navController.previousBackStackEntry?.savedStateHandle?.set(Constants.GroupingType, type); dismiss() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(id = type.toResource()))
                        }
                    }
                }
            }
        }
    }
}