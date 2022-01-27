package com.noto.app.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.noto.app.util.localDataSourceModule
import com.noto.app.domain.model.Folder
import com.noto.app.domain.source.LocalFolderDataSource
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
class LocalFolderDataSourceTest : KoinTest {

    private lateinit var source: LocalFolderDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        unloadKoinModules(localDataSourceModule)
        loadKoinModules(inMemoryDatabaseModule)
        source = get()
        runBlocking {
            source.clearFolders()
        }
    }

    @Test
    fun get_all_libraries_should_return_an_empty_list() = runBlockingTest {
        val dbLibraries = source.getFolders()
            .first()

        assertTrue { dbLibraries.isEmpty() }
    }

    @Test
    fun create_new_library_should_insert_new_library_in_the_database() = runBlockingTest {
        val library = createLibrary()
        source.createFolder(library)

        val dbLibraries = source.getFolders()
            .first()

        assertContains(dbLibraries, library.copy(id = 7))
    }

    @Test
    fun get_library_by_id_should_return_a_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createFolder(library)

        val dbLibrary = source.getFolderById(folderId = 9)
            .first()

        assertEquals(library.copy(id = 9), dbLibrary)
    }

    @Test
    fun update_library_should_update_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createFolder(library)

        val updatedLibrary = library.copy(id = 8, title = "Code")
        source.updateFolder(updatedLibrary)

        val dbLibrary = source.getFolderById(8)
            .first()

        assertEquals(updatedLibrary, dbLibrary)
    }

    @Test
    fun delete_library_should_remove_library_with_matching_id() = runBlockingTest {
        val library = createLibrary()
        source.createFolder(library)

        val dbLibraries = source.getFolders()
            .first()

        assertContains(dbLibraries, library.copy(id = 6))

        source.deleteFolder(library.copy(id = 6))

        val updatedDbLibraries = source.getFolders()
            .first()

        assertTrue { updatedDbLibraries.isEmpty() }
    }

    @Test
    fun clear_libraries_should_remove_all_libraries() = runBlockingTest {
        repeat(5) {
            val library = createLibrary(title = "Work $it")
            source.createFolder(library)
        }

        val dbLibraries = source.getFolders()
            .first()

        assertTrue { dbLibraries.count() == 5 }

        source.clearFolders()

        val updatedDbLibraries = source.getFolders()
            .first()

        assertTrue { updatedDbLibraries.isEmpty() }
    }

    private fun createLibrary(title: String = "Work") = Folder(title = title, position = 0)
}