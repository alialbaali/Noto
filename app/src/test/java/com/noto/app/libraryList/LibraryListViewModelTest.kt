package com.noto.app.libraryList

import com.noto.app.repository.fake.FakeLibraryRepository
import com.noto.domain.repository.LibraryRepository
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryListModule = module {

    single<LibraryRepository> { FakeLibraryRepository() }

//    single<SharedPreferences> { mockkClass(SharedPreferences::class) }

    viewModel { LibraryListViewModel(get(), get()) }

}

class LibraryListViewModelTest {


}