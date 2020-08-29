package com.noto.app

import android.app.Application
import com.noto.app.di.appModule
import com.noto.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
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
                appModule,
                remoteDataSourceModule,
                repositoryModule,
                libraryUseCasesModule,
                notoUseCasesModule,
                userUseCasesModule,
                localDataSourceModule
            )
        }
    }
}