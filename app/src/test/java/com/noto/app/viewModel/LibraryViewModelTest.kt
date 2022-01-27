package com.noto.app.viewModel

import com.noto.app.util.appModule
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.fakeLocalDataSourceModule
import com.noto.app.library.LibraryViewModel
import com.noto.app.testRepositoryModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.get

@ExperimentalCoroutinesApi
class LibraryViewModelTest : StringSpec(), KoinTest {

    private lateinit var viewModel: LibraryViewModel
    private lateinit var libraryRepository: LibraryRepository
    private lateinit var noteRepository: NoteRepository

    init {
        beforeEach {
            startKoin {
                modules(appModule, testRepositoryModule, fakeLocalDataSourceModule)
            }
            libraryRepository = get()
            libraryRepository.createLibrary(Folder(id = 1L, title = "Work", position = 0))
            noteRepository = get()
            repeat(3) {
                val note = Note(id = it.toLong(), libraryId = 1, title = "Title $it", body = "Body $it", position = 0)
                noteRepository.createNote(note)
            }
            repeat(5) {
                val note = Note(id = it.toLong(), libraryId = 1, title = "Title $it", body = "Body $it", position = 0, isArchived = true)
                noteRepository.createNote(note)
            }
            viewModel = get { parametersOf(1L) }
        }

        afterEach {
            stopKoin()
        }

        "get library should return new entity when id is 0" {
            viewModel = get { parametersOf(0L) }
            val library = viewModel.state
                .map { it.library }
                .first()
            library.id shouldBeExactly 0L
            library.title.shouldBeBlank()
        }

        "create library should insert new library with provided id" {
            val library = viewModel.state
                .map { it.library }
                .first()
            library.id shouldBeExactly 1L
            library.title shouldBeEqualIgnoringCase "Work"
        }

        "update library should update library with matching id" {
            viewModel.createOrUpdateLibrary("UpdatedTitle")
            val library = libraryRepository.getLibraryById(1)
                .first()
            library.id shouldBeExactly 1L
            library.title shouldBeEqualIgnoringCase "UpdatedTitle"
        }

        "delete library should remove the current library in the viewModel" {
            viewModel.deleteLibrary()
            libraryRepository.getLibraries()
                .first()
                .shouldBeEmpty()
        }

        "select noto color should set value true for that color and false for the rest" {
            viewModel.selectNotoColor(NotoColor.Blue)
            val notoColors = viewModel.notoColors
                .first()

            notoColors shouldContain (NotoColor.Blue to true)
            notoColors.filterNot { it.first == NotoColor.Blue }
                .forEach { it.second.shouldBeFalse() }
        }

        "get layout manager should return linear by default" {
            viewModel.state
                .map { it.library.layoutManager }
                .first() shouldBe Layout.Linear
        }

        "update layout manager to grid" {
            viewModel.updateLayout(Layout.Grid)
            viewModel.state
                .map { it.library.layoutManager }
                .first() shouldBe Layout.Grid
        }

        "get notes should return an empty list when library id is 0" {
            viewModel = get { parametersOf(0L) }
            viewModel.state
                .map { it.notes }
                .first()
                .shouldBeEmpty()
        }

        "get notes should return a non-empty list after inserting some elements" {
            viewModel.state
                .map { it.notes }
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(3)
        }

        "get archived notes should return an empty list when library id is 0" {
            viewModel = get { parametersOf(0L) }
            viewModel.state
                .map { it.archivedNotes }
                .first()
                .shouldBeEmpty()
        }

        "get archived notes should return a non-empty list after inserting some elements" {
            viewModel.state
                .map { it.archivedNotes }
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(5)
        }

        "search notes should return a non-empty list when search term is blank" {
            viewModel.searchNotes("")
            viewModel.state
                .map { it.notes }
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(3)
        }

        "search notes should return notes with matching title" {
            viewModel.searchNotes("Title 1")
            viewModel.state
                .map { it.notes }
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
        }

        "search notes should return notes with matching body" {
            viewModel.searchNotes("Body 2")
            viewModel.state
                .map { it.notes }
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
        }
    }
}