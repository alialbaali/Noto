package com.noto.app.di

import android.content.Context
import com.noto.app.SHARED_PREFERENCES_NAME
import com.noto.app.library.LibraryListViewModel
import com.noto.app.library.LibraryViewModel
import com.noto.app.noto.NotoViewModel
import com.noto.app.sign.SignSharedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { LibraryListViewModel(get()) }

    viewModel { LibraryViewModel(get(), get()) }

    viewModel { NotoViewModel(get(), get()) }

    viewModel { SignSharedViewModel(get()) }

    single { androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
}