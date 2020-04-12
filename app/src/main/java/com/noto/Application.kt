package com.noto

import android.app.Application
import android.content.Context
import com.noto.note.di.noteModule
import com.noto.todo.di.todoModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

const val SHARED_PREFERENCES_NAME = "Noto Shared Preferences"

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                noteModule,
                todoModule,
                module {
                    single { androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
                }
            )
        }
    }
}