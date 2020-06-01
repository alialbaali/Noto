package com.noto.di

import android.content.Context
import com.noto.SHARED_PREFERENCES_NAME
import com.noto.label.LabelListViewModel
import com.noto.library.LibraryListViewModel
import com.noto.library.LibraryViewModel
import com.noto.noto.NotoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { LibraryListViewModel(get()) }

    viewModel { LibraryViewModel(get(), get()) }

    viewModel { NotoViewModel(get(), get(), get()) }

    viewModel { LabelListViewModel(get()) }

    single { androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
}