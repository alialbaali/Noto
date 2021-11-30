package com.noto.app.widget

import android.content.Intent
import android.widget.RemoteViewsService

class LibraryListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = LibraryListRemoteViewsFactory(applicationContext, intent)
}