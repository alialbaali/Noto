package com.noto.app.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListFilteringDialogFragmentBinding
import com.noto.app.domain.model.FilteringType
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

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
    ): View = NoteListFilteringDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.filtering)

        when (filteringType) {
            FilteringType.Inclusive, null -> rbInclusive.isChecked = true
            FilteringType.Exclusive -> rbExclusive.isChecked = true
            FilteringType.Strict -> rbStrict.isChecked = true
        }

        rbInclusive.setOnClickListener {
            onClick(FilteringType.Inclusive)
            dismiss()
        }

        rbExclusive.setOnClickListener {
            onClick(FilteringType.Exclusive)
            dismiss()
        }

        rbStrict.setOnClickListener {
            onClick(FilteringType.Strict)
            dismiss()
        }
    }
}