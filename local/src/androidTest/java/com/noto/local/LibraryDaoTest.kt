package com.noto.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.noto.domain.model.Library
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

private val localModule = module {

    single<NotoDatabase> { Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), NotoDatabase::class.java).build() }

    single<LibraryDao> { get<NotoDatabase>().libraryDao }

}

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class LibraryDaoTest : KoinTest {

    private val libraryDao by inject<LibraryDao>()

    @Before
    fun setUp() {
        startKoin {
            modules(localModule)
        }
    }

    @After
    fun tearDown() {
        val database by inject<NotoDatabase>()
        database.close()
    }


    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun createLibrary() = runBlockingTest {
        val library = Library(libraryTitle = "Coding", libraryPosition = 0, notoColor = NotoColor.TEAL, notoIcon = NotoIcon.CODE)

        libraryDao.createLibrary(library)

        val libraries = libraryDao.getLibraries().singleOrNull()

        libraries?.let { list ->
            list shouldContain library
        }



    }





}