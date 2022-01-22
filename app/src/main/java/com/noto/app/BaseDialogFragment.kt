package com.noto.app

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noto.app.util.dp

open class BaseDialogFragment(private val isCollapsable: Boolean = false) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            (this as BottomSheetDialog).apply {
                if (isCollapsable) {
                    behavior.peekHeight = 500.dp
                } else {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        dialog?.window?.decorView?.systemUiVisibility = 0
    }
}