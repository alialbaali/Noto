package com.noto.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.noto.app.AppViewModel
import com.noto.app.data.database.NotoDatabase
import com.noto.app.data.repository.*
import com.noto.app.domain.repository.*
import com.noto.app.domain.source.LocalFolderDataSource
import com.noto.app.domain.source.LocalLabelDataSource
import com.noto.app.domain.source.LocalNoteDataSource
import com.noto.app.domain.source.LocalNoteLabelDataSource
import com.noto.app.filtered.FilteredViewModel
import com.noto.app.folder.FolderViewModel
import com.noto.app.label.LabelViewModel
import com.noto.app.main.MainViewModel
import com.noto.app.note.NotePagerViewModel
import com.noto.app.note.NoteViewModel
import com.noto.app.settings.SettingsViewModel
import com.noto.app.widget.FolderListWidgetConfigViewModel
import com.noto.app.widget.NoteListWidgetConfigViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"
private val Context.dataStore by preferencesDataStore(name = DataStoreName)

val appModule = module {

    viewModel { MainViewModel(get(), get(), get()) }

    viewModel { FolderViewModel(get(), get(), get(), get(), get(), it.get(), it.getOrNull() ?: longArrayOf()) }

    viewModel { NoteViewModel(get(), get(), get(), get(), get(), it[0], it[1], it.getOrNull(), it.getOrNull() ?: longArrayOf()) }

    viewModel { AppViewModel(get(), get(), get()) }

    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }

    viewModel { LabelViewModel(get(), get(), it[0], it[1]) }

    viewModel { FolderListWidgetConfigViewModel(it.get(), get(), get(), get()) }

    viewModel { NoteListWidgetConfigViewModel(it.get(), get(), get(), get(), get(), get()) }

    viewModel { NotePagerViewModel(get(), get(),get(), it[0], it[1], it[2], it[3]) }

    viewModel { FilteredViewModel(get(), get(), get(), get(), get(), it.get()) }

}

val repositoryModule = module {

    single<FolderRepository> { FolderRepositoryImpl(get()) }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<LabelRepository> { LabelRepositoryImpl(get()) }

    single<NoteLabelRepository> { NoteLabelRepositoryImpl(get()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

}

val localDataSourceModule = module {

    single<LocalFolderDataSource> { NotoDatabase.getInstance(androidContext()).folderDao }

    single<LocalNoteDataSource> { NotoDatabase.getInstance(androidContext()).noteDao }

    single<LocalLabelDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<LocalNoteLabelDataSource> { NotoDatabase.getInstance(androidContext()).noteLabelDao }

    single<DataStore<Preferences>> { androidContext().dataStore }

}