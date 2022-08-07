package com.noto.app.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.noto.app.domain.model.Folder
import com.noto.app.util.Constants
import com.noto.app.util.enabledComponentName

@RequiresApi(Build.VERSION_CODES.N)
class CreateNoteTileService : TileService() {
    override fun onClick() {
        val intent = Intent(Constants.Intent.ActionCreateNote, null).apply {
            component = enabledComponentName
            putExtra(Constants.FolderId, Folder.GeneralFolderId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        unlockAndRun {
            startActivityAndCollapse(intent)
        }
    }
}