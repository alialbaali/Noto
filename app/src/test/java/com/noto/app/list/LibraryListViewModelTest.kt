package com.noto.app.list

import android.content.SharedPreferences
import com.noto.app.library.LibraryListViewModel
import com.noto.app.repository.fake.FakeLibraryRepository
import com.noto.domain.repository.LibraryRepository
import com.tfcporciuncula.flow.FlowSharedPreferences
import io.mockk.mockkClass
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryListModule = module {

    single<LibraryRepository> { FakeLibraryRepository() }

    single<SharedPreferences> { mockkClass(SharedPreferences::class) }

    single { FlowSharedPreferences(get()) }

    viewModel { LibraryListViewModel(get(), get()) }

}

class LibraryListViewModelTest {


}