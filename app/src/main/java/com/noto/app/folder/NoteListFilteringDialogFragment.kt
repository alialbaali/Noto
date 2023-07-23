package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.text.toSpannable
import androidx.navigation.fragment.navArgs
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.MediumSubtitle
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.FilteringType
import com.noto.app.toColor
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.toAnnotatedString
import com.noto.app.util.toDescriptionResource
import com.noto.app.util.toResource
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListFilteringDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListFilteringDialogFragmentArgs>()

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
                val types = FilteringType.entries
                val filteringType by savedStateHandle.getStateFlow<FilteringType?>(key = Constants.FilteringType, initialValue = null)
                    .collectAsState()

                BottomSheetDialog(title = stringResource(R.string.filtering), headerColor = folder.color.toColor()) {
                    types.forEach { type ->
                        val typeDescription = remember(type) {
                            context.getText(type.toDescriptionResource()).toSpannable().toAnnotatedString()
                        }

                        SelectableDialogItem(
                            selected = type == (filteringType ?: folder.filteringType),
                            onClick = { navController.previousBackStackEntry?.savedStateHandle?.set(Constants.FilteringType, type); dismiss() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.extraSmall)) {
                                Text(text = stringResource(id = type.toResource()))
                                MediumSubtitle(text = typeDescription)
                            }
                        }
                    }
                }
            }
        }
    }
}