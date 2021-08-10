package com.noto.app.data.source

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.noto.app.data.database.NotoDatabase
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class LocalNoteDataSourceTest {

    private lateinit var source: LocalNoteDataSource
    private lateinit var database: NotoDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    var coroutineRule = CoroutineTestRule()

    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NotoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        source = database.noteDao
    }

    @After
    fun closeDatabase() = database.close()

//    @Test
//    fun get_all_notes_should_return_an_empty_list() = coroutineRule.dispatcher.runBlockingTest {
//        val notes = source.getNotesByLibraryId(libraryId = 1)
//            .single()
//
//        assertTrue { notes.isNotEmpty() }
//    }
}