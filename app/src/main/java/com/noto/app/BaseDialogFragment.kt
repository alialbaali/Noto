package com.noto.app

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noto.app.util.applyNightModeConfiguration
import com.noto.app.util.applySystemBarsColorsForApiLessThan23
import com.noto.app.util.dp

open class BaseDialogFragment(private val isCollapsable: Boolean = false) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            if (isCollapsable) {
                behavior.peekHeight = 500.dp
            } else {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let { window ->
            context?.applyNightModeConfiguration(window)
            context?.applySystemBarsColorsForApiLessThan23(window)
        }
    }
}