package com.noto.app.note

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.noto.app.components.TransparentActivity
import com.noto.app.util.Constants

@RequiresApi(Build.VERSION_CODES.N)
class QuickNoteTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Constants.Intent.ActionQuickNote, null, this, TransparentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapse(intent)
        }
    }
}