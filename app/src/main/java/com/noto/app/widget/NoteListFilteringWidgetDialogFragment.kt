package com.noto.app.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.text.toSpannable
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.MediumSubtitle
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.FilteringType
import com.noto.app.util.toAnnotatedString
import com.noto.app.util.toDescriptionResource
import com.noto.app.util.toResource

class NoteListFilteringWidgetDialogFragment constructor() : BaseDialogFragment() {
    private var onClick: (FilteringType) -> Unit = {}
    private var filteringType: FilteringType? = null

    constructor(filteringType: FilteringType, onClick: (FilteringType) -> Unit) : this() {
        this.filteringType = filteringType
        this.onClick = onClick
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val types = remember { FilteringType.values() }

                BottomSheetDialog(title = stringResource(R.string.filtering)) {
                    types.forEach { type ->
                        val typeDescription = remember(type) {
                            context.getText(type.toDescriptionResource()).toSpannable().toAnnotatedString()
                        }

                        SelectableDialogItem(
                            selected = filteringType == type,
                            onClick = {
                                onClick(type)
                                dismiss()
                            },
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