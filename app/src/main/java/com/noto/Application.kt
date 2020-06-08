package com.noto

import android.app.Application
import android.content.Context
import com.alialbaali.noto.di.*
import com.noto.di.appModule
import net.danlew.android.joda.JodaTimeAndroid
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
        JodaTimeAndroid.init(this)

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
                labelUseCasesModule,
                localDataSourceModule
            )
        }
    }
}