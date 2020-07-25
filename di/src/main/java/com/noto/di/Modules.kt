package com.noto.di

import android.content.Context
import com.noto.data.repository.LabelRepositoryImpl
import com.noto.data.repository.LibraryRepositoryImpl
import com.noto.data.repository.NotoRepositoryImpl
import com.noto.domain.repository.LabelRepository
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NotoRepository
import com.noto.local.LabelDao
import com.noto.local.LibraryDao
import com.noto.local.NotoDao
import com.noto.local.NotoDatabase
import com.tfcporciuncula.flow.FlowSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val SHARED_PREFERENCES_NAME = "Noto Shared Preferences"

val repositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get<LibraryDao>()) }

    single<NotoRepository> { NotoRepositoryImpl(get<NotoDao>()) }

    single<LabelRepository> { LabelRepositoryImpl(get<LabelDao>()) }

}

val localDataSourceModule = module {

    single<LibraryDao> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<NotoDao> { NotoDatabase.getInstance(androidContext()).notoDao }

    single<LabelDao> { NotoDatabase.getInstance(androidContext()).labelDao }

    single { FlowSharedPreferences(androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)) }

}