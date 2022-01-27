package com.noto.app.widget

import android.content.Intent
import android.widget.RemoteViewsService

class FolderListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = FolderRemoteViewsFactory(applicationContext, intent)
}