package com.noto.app.di

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import com.noto.app.MainViewModel
import com.noto.app.data.database.NotoDatabase
import com.noto.app.data.repository.LabelRepositoryImpl
import com.noto.app.data.repository.LibraryRepositoryImpl
import com.noto.app.data.repository.NoteRepositoryImpl
import com.noto.app.data.source.LocalStorageImpl
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LabelLocalDataSource
import com.noto.app.domain.source.LibraryLocalDataSource
import com.noto.app.domain.source.LocalStorage
import com.noto.app.domain.source.NoteLocalDataSource
import com.noto.app.label.LabelViewModel
import com.noto.app.librarylist.LibraryListViewModel
import com.noto.app.note.NoteViewModel
import com.noto.app.notelist.NoteListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"

val appModule = module {

    viewModel { LibraryListViewModel(get(), get()) }

    viewModel { NoteListViewModel(get(), get(), get(), it.get()) }

    viewModel { NoteViewModel(get(), get(), it[0], it[1], it.getOrNull()) }

    viewModel { LabelViewModel(get()) }

    viewModel { MainViewModel(get()) }

}

val repositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get()) }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<LabelRepository> { LabelRepositoryImpl(get()) }

}

val localDataSourceModule = module {

    single<LibraryLocalDataSource> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<NoteLocalDataSource> { NotoDatabase.getInstance(androidContext()).noteDao }

    single<LabelLocalDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<DataStore<Preferences>> { androidContext().createDataStore(DataStoreName) }

    single<LocalStorage> { LocalStorageImpl(get()) }

}