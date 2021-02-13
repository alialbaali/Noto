package com.noto.app

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialog

}