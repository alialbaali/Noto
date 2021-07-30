package com.noto.app.library

import com.noto.app.repository.fake.FakeLibraryRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.notelist.NoteListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val libraryModule = module {

    single<LibraryRepository> { FakeLibraryRepository() }

    viewModel { NoteListViewModel(get(), get(), get()) }

}

class LibraryViewModelTest {
}