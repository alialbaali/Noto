package com.noto

import android.app.Application
import timber.log.Timber

class Application : Application() {
    init {
        run {
            Timber.plant(Timber.DebugTree())
        }
    }
}