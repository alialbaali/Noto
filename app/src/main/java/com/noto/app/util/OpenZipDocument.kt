package com.noto.app.util

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class OpenZipDocument : ActivityResultContracts.OpenDocument() {
    override fun createIntent(context: Context, input: Array<out String>): Intent {
        super.createIntent(context, input)
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
            .setType("application/zip")
    }
}