package com.noto.app

import android.app.Application
import com.noto.app.label.LabelViewModel
import com.noto.app.library.LibraryListViewModel
import com.noto.app.library.LibraryViewModel
import com.noto.app.noto.NotoViewModel
import com.noto.di.localDataSourceModule
import com.noto.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class NotoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@NotoApplication)
            androidLogger()
            modules(
                appModule,
                repositoryModule,
                localDataSourceModule
            )
        }
    }
}

val appModule = module {

    viewModel { LibraryListViewModel(get(), get()) }

    viewModel { LibraryViewModel(get(), get(), get()) }

    viewModel { NotoViewModel(get(), get()) }

    viewModel { LabelViewModel(get()) }

    viewModel { MainViewModel(get()) }

}