package com.noto.app

import com.noto.app.domain.source.LocalLibraryDataSource
import com.noto.app.domain.source.LocalNoteDataSource
import com.noto.app.unit.data.fake.FakeLocalLibraryDataSource
import com.noto.app.unit.data.fake.FakeLocalNoteDataSource
import org.koin.dsl.module

val fakeLocalDataSourceModule = module {

    single<LocalLibraryDataSource> { FakeLocalLibraryDataSource() }

    single<LocalNoteDataSource> { FakeLocalNoteDataSource() }

}