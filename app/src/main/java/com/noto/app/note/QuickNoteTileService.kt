package com.noto.app.note

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.noto.app.components.TransparentActivity
import com.noto.app.util.Constants
import com.noto.app.util.PendingIntentFlags

private const val PendingIntentRequestCode = 0

@RequiresApi(Build.VERSION_CODES.N)
class QuickNoteTileService : TileService() {
    @Suppress("DEPRECATION")
    override fun onClick() {
        val intent = Intent(Constants.Intent.ActionQuickNote, null, this, TransparentActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, PendingIntentRequestCode, intent, PendingIntentFlags)
        unlockAndRun {
            if (Build.VERSION.SDK_INT >= 34) {
                startActivityAndCollapse(pendingIntent)
            } else {
                startActivityAndCollapse(intent)
            }
        }
    }
}