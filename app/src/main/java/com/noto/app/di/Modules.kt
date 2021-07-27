package com.noto.app.di

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import com.noto.app.data.LabelRepositoryImpl
import com.noto.app.data.LibraryRepositoryImpl
import com.noto.app.data.NoteRepositoryImpl
import com.noto.app.data.database.NotoDatabase
import com.noto.app.data.source.LocalStorageImpl
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LibraryLocalDataSource
import com.noto.app.domain.source.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"

val repositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get()) }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<LabelRepository> { LabelRepositoryImpl(get()) }

}

val localDataSourceModule = module {

    single<LibraryLocalDataSource> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<com.noto.app.domain.source.NoteLocalDataSource> { NotoDatabase.getInstance(androidContext()).notoDao }

    single<com.noto.app.domain.source.LabelLocalDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<DataStore<Preferences>> { androidContext().createDataStore(DataStoreName) }

    single<LocalStorage> { LocalStorageImpl(get()) }

}