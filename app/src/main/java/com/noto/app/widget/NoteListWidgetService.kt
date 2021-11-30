package com.noto.app.widget

import android.content.Intent
import android.widget.RemoteViewsService

class NoteListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory = NoteListRemoteViewsFactory(applicationContext, intent)
}