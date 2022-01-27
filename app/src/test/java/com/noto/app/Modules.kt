package com.noto.app

import com.noto.app.data.repository.LibraryRepositoryImpl
import com.noto.app.data.repository.NoteRepositoryImpl
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalFolderDataSource
import com.noto.app.domain.source.LocalNoteDataSource
import com.noto.app.data.fake.FakeLocalFolderDataSource
import com.noto.app.data.fake.FakeLocalNoteDataSource
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val fakeLocalDataSourceModule = module {

    single<LocalFolderDataSource> { FakeLocalFolderDataSource() }

    single<LocalNoteDataSource> { FakeLocalNoteDataSource() }

}

val testRepositoryModule = module {

    single<LibraryRepository> { LibraryRepositoryImpl(get(), Dispatchers.Unconfined) }

    single<NoteRepository> { NoteRepositoryImpl(get(), Dispatchers.Unconfined) }

}