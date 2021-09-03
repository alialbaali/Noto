package com.noto.app.data.repository

import com.noto.app.util.repositoryModule
import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.fakeLocalDataSourceModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import kotlinx.coroutines.flow.single
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class LibraryRepositoryTest : StringSpec(), KoinTest {

    private lateinit var repository: LibraryRepository

    init {
        beforeTest {
            startKoin {
                modules(fakeLocalDataSourceModule, repositoryModule)
            }
            repository = get()
        }

        afterTest {
            stopKoin()
        }

        "get all libraries should be empty" {
            repository.getLibraries()
                .single()
                .shouldBeEmpty()
        }

        "create library should insert a new library" {
            val library = Library(id = 1, title = "Work", position = 0)
            repository.createLibrary(library)
            repository.getLibraries()
                .single()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
                .shouldContain(library)
        }

        "update library should update existing library" {
            val library = Library(id = 1, title = "Work", position = 0)
            repository.createLibrary(library)

            val updatedLibrary = library.copy(title = "Home")
            repository.updateLibrary(updatedLibrary)

            repository.getLibraryById(libraryId = 1)
                .single()
                .title shouldBeEqualIgnoringCase "Home"
        }

        "delete library should remove existing library" {
            val library = Library(id = 1, title = "Work", position = 0)

            repository.createLibrary(library)
            repository.getLibraries()
                .single()
                .shouldNotBeEmpty()

            repository.deleteLibrary(library)
            repository.getLibraries()
                .single()
                .shouldBeEmpty()
        }
    }
}