package com.noto.app

import androidx.room.Room
import com.noto.app.data.database.NotoDatabase
import com.noto.app.domain.source.LocalLibraryDataSource
import com.noto.app.domain.source.LocalNoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val inMemoryDatabaseModule = module {

    single<NotoDatabase> { Room.inMemoryDatabaseBuilder(androidContext(), NotoDatabase::class.java).build() }

    single<LocalLibraryDataSource> { get<NotoDatabase>().libraryDao }

    single<LocalNoteDataSource> { get<NotoDatabase>().noteDao }

}