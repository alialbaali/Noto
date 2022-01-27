package com.noto.app.data.repository

import com.noto.app.util.repositoryModule
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.FolderRepository
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

    private lateinit var repository: FolderRepository

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
            repository.getFolders()
                .single()
                .shouldBeEmpty()
        }

        "create library should insert a new library" {
            val library = Folder(id = 1, title = "Work", position = 0)
            repository.createFolder(library)
            repository.getFolders()
                .single()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
                .shouldContain(library)
        }

        "update library should update existing library" {
            val library = Folder(id = 1, title = "Work", position = 0)
            repository.createFolder(library)

            val updatedLibrary = library.copy(title = "Home")
            repository.updateFolder(updatedLibrary)

            repository.getFolderById(folderId = 1)
                .single()
                .title shouldBeEqualIgnoringCase "Home"
        }

        "delete library should remove existing library" {
            val library = Folder(id = 1, title = "Work", position = 0)

            repository.createFolder(library)
            repository.getFolders()
                .single()
                .shouldNotBeEmpty()

            repository.deleteFolder(library)
            repository.getFolders()
                .single()
                .shouldBeEmpty()
        }
    }
}