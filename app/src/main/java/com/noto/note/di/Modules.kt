package com.noto.note.di

import com.noto.database.NotoDatabase
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import com.noto.note.viewModel.NoteViewModel
import com.noto.note.viewModel.NotebookListViewModel
import com.noto.note.viewModel.NotebookViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val noteModule = module {

    viewModel { NotebookListViewModel(get()) }

    viewModel { NotebookViewModel(get(), get()) }

    viewModel { NoteViewModel(get()) }

    single { NotebookRepository(get(), get()) }

    single { NoteRepository(get()) }

    single { NotoDatabase.getInstance(androidContext()).notebookDao }

    single { NotoDatabase.getInstance(androidContext()).noteDao }

}
