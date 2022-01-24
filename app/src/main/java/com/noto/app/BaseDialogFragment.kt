package com.noto.app

import android.app.Dialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.noto.app.util.colorResource

open class BaseDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            (this as BottomSheetDialog).apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                dialog?.window?.decorView?.systemUiVisibility = 0
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    context?.let { context ->
                        dialog?.window?.statusBarColor = context.colorResource(android.R.color.black)
                        dialog?.window?.navigationBarColor = context.colorResource(android.R.color.black)
                    }
                }
            }
        }
    }
}