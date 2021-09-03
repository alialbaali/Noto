package com.noto.app.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.noto.app.util.localDataSourceModule
import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LocalLibraryDataSource
import com.noto.app.inMemoryDatabaseModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class LocalLibraryDataSourceTest : KoinTest {

    private lateinit var source: LocalLibraryDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        unloadKoinModules(localDataSourceModule)
        loadKoinModules(inMemoryDatabaseModule)
        source = get()
        runBlocking {
            source.clearLibraries()
        }
    }

    @Test
    fun get_all_libraries_should_return_an_empty_list() = runBlockingTest {
        val dbLibraries = source.getLibraries()
            .first()

        assertTrue { dbLibraries.isEmpty() }
    }

    @Test
    fun create_new_library_should_insert_new_library_in_the_database() = runBlockingTest {
        val library = createLibrary()
        source.createLibrary(library)

        val dbLibraries = source.getLibraries()
            .first()

        assertContains(dbLibraries, library.copy(id = 7))
    }

    @Test
    fun get_library_by_id_should_return_a_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createLibrary(library)

        val dbLibrary = source.getLibraryById(libraryId = 9)
            .first()

        assertEquals(library.copy(id = 9), dbLibrary)
    }

    @Test
    fun update_library_should_update_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createLibrary(library)

        val updatedLibrary = library.copy(id = 8, title = "Code")
        source.updateLibrary(updatedLibrary)

        val dbLibrary = source.getLibraryById(8)
            .first()

        assertEquals(updatedLibrary, dbLibrary)
    }

    @Test
    fun delete_library_should_remove_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createLibrary(library)

        val dbLibraries = source.getLibraries()
            .first()

        assertContains(dbLibraries, library.copy(id = 6))

        source.deleteLibrary(library.copy(id = 6))

        val updatedDbLibraries = source.getLibraries()
            .first()

        assertTrue { updatedDbLibraries.isEmpty() }
    }

    @Test
    fun clear_libraries_should_remove_all_libraries() = runBlockingTest {
        repeat(5) {
            val library = createLibrary(title = "Work $it")
            source.createLibrary(library)
        }

        val dbLibraries = source.getLibraries()
            .first()

        assertTrue { dbLibraries.count() == 5 }

        source.clearLibraries()

        val updatedDbLibraries = source.getLibraries()
            .first()

        assertTrue { updatedDbLibraries.isEmpty() }
    }

    private fun createLibrary(title: String = "Work") = Library(title = title, position = 0)
}