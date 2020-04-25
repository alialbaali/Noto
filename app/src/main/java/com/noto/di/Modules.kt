package com.noto.di

import com.noto.database.NotoDatabase
import com.noto.repository.BlockRepository
import com.noto.repository.LabelRepository
import com.noto.repository.LibraryRepository
import com.noto.repository.NotoRepository
import com.noto.viewModel.*
//import com.noto.repository.NoteRepository
//import com.noto.repository.NotoRepository
//import com.noto.repository.SubTodoRepository
//import com.noto.repository.TodoRepository
//import com.noto.viewModel.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { NotoDatabase.getInstance(androidContext()).libraryDao }

    single { NotoDatabase.getInstance(androidContext()).notoDao }

    single { NotoDatabase.getInstance(androidContext()).blockDao }

    single { NotoDatabase.getInstance(androidContext()).labelDao }

    single { LibraryRepository(get(), get()) }

    viewModel { LibraryListViewModel(get()) }

    viewModel { LibraryViewModel(get(), get()) }

    viewModel { NotoViewModel(get(), get(), get()) }

    viewModel { LabelListViewModel(get()) }

    viewModel { CalendarViewModel() }

    single { NotoRepository(get(), get()) }

    single { BlockRepository(get()) }

    single { LabelRepository(get()) }
}