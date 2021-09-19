package com.noto.app.util

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import com.noto.app.AppViewModel
import com.noto.app.data.database.NotoDatabase
import com.noto.app.data.repository.LabelRepositoryImpl
import com.noto.app.data.repository.LibraryRepositoryImpl
import com.noto.app.data.repository.NoteLabelRepositoryImpl
import com.noto.app.data.repository.NoteRepositoryImpl
import com.noto.app.data.source.LocalStorageImpl
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.*
import com.noto.app.label.LabelViewModel
import com.noto.app.library.LibraryViewModel
import com.noto.app.main.MainViewModel
import com.noto.app.note.NoteViewModel
import com.noto.app.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"

val appModule = module {

    viewModel { MainViewModel(get(), get(), get()) }

    viewModel { LibraryViewModel(get(), get(), get(), get(), it.get()) }

    viewModel { NoteViewModel(get(), get(), get(), get(), get(), it[0], it[1], it.getOrNull()) }

    viewModel { AppViewModel(get()) }

    viewModel { SettingsViewModel(get()) }

    viewModel { LabelViewModel(get(), get(), it[0], it[1]) }
}

val repositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get()) }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<LabelRepository> { LabelRepositoryImpl(get()) }

    single<NoteLabelRepository> { NoteLabelRepositoryImpl(get()) }

}

val localDataSourceModule = module {

    single<LocalLibraryDataSource> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<LocalNoteDataSource> { NotoDatabase.getInstance(androidContext()).noteDao }

    single<LocalLabelDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<LocalNoteLabelDataSource> { NotoDatabase.getInstance(androidContext()).noteLabelDao }

    single<DataStore<Preferences>> { androidContext().createDataStore(DataStoreName) }

    single<LocalStorage> { LocalStorageImpl(get()) }

}