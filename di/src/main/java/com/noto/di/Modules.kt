package com.noto.di

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import com.noto.data.LabelRepositoryImpl
import com.noto.data.LibraryRepositoryImpl
import com.noto.data.NotoRepositoryImpl
import com.noto.domain.local.LabelLocalDataSource
import com.noto.domain.local.LibraryLocalDataSource
import com.noto.domain.local.LocalStorage
import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.repository.LabelRepository
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NotoRepository
import com.noto.local.LocalStorageImpl
import com.noto.local.NotoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"

val repositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get<LibraryLocalDataSource>()) }

    single<NotoRepository> { NotoRepositoryImpl(get<NotoLocalDataSource>()) }

    single<LabelRepository> { LabelRepositoryImpl(get<LabelLocalDataSource>()) }

}

val localDataSourceModule = module {

    single<LibraryLocalDataSource> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<NotoLocalDataSource> { NotoDatabase.getInstance(androidContext()).notoDao }

    single<LabelLocalDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<DataStore<Preferences>> { androidContext().createDataStore(DataStoreName) }

    single<LocalStorage> { LocalStorageImpl(get()) }

}