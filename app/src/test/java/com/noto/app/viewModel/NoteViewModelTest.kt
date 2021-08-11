package com.noto.app.viewModel

import com.noto.app.di.appModule
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.fakeLocalDataSourceModule
import com.noto.app.note.NoteViewModel
import com.noto.app.testRepositoryModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.get

class NoteViewModelTest : StringSpec(), KoinTest {

    private lateinit var viewModel: NoteViewModel
    private lateinit var libraryRepository: LibraryRepository
    private lateinit var noteRepository: NoteRepository

    init {
        beforeEach {
            startKoin {
                modules(appModule, testRepositoryModule, fakeLocalDataSourceModule)
            }
            libraryRepository = get()
            noteRepository = get()
            libraryRepository.createLibrary(Library(id = 1, title = "Work", position = 0))
            libraryRepository.createLibrary(Library(id = 2, title = "Home", position = 0))
            noteRepository.createNote(Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0))
            viewModel = get { parametersOf(1L, 1L) }
        }

        afterEach {
            stopKoin()
        }

        "get library should return library with matching id" {
            val library = viewModel.library
                .first()
            library.id shouldBeExactly 1
            library.title shouldBeEqualIgnoringCase "Work"
        }

        "get note should return a default note when note id is 0" {
            viewModel = get { parametersOf(1L, 0L) }
            val note = viewModel.note
                .first()
            note.id shouldBeExactly 0L
            note.title.shouldBeBlank()
        }

        "get note should return a note with matching id" {
            val note = viewModel.note
                .first()
            note.id shouldBeExactly 1L
            note.title shouldBeEqualIgnoringCase "Title"
        }

        "update note should update the note with matching id" {
            viewModel.createOrUpdateNote("Title 2", "Body 2")
            val note = noteRepository.getNoteById(noteId = 1)
                .first()
            note.id shouldBeExactly 1L
            note.title shouldBeEqualIgnoringCase "Title 2"
            note.body shouldBeEqualIgnoringCase "Body 2"
        }

        "delete note should remove note with matching id" {
            viewModel.deleteNote()
            noteRepository.getNotesByLibraryId(libraryId = 1)
                .first()
                .shouldBeEmpty()
        }

        "toggle note is archived should set note archived property to true" {
            noteRepository.getNoteById(noteId = 1)
                .first()
                .isArchived
                .shouldBeFalse()
            viewModel.toggleNoteIsArchived()
            noteRepository.getNoteById(noteId = 1)
                .first()
                .isArchived
                .shouldBeTrue()
        }

        "toggle note is starred should set note starred property to true" {
            noteRepository.getNoteById(noteId = 1)
                .first()
                .isStarred
                .shouldBeFalse()
            viewModel.toggleNoteIsStarred()
            noteRepository.getNoteById(noteId = 1)
                .first()
                .isStarred
                .shouldBeTrue()
        }

        "set note reminder should set note reminder with provided value" {
            val now = Clock.System.now()
            noteRepository.getNoteById(noteId = 1)
                .first()
                .reminderDate
                .shouldBeNull()
            viewModel.setNoteReminder(now)
            noteRepository.getNoteById(noteId = 1)
                .first()
                .reminderDate
                .shouldNotBeNull()
                .shouldBe(now)
        }

        "duplicate note should create new note with same data" {
            noteRepository.getNotesByLibraryId(libraryId = 1L)
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
            viewModel.duplicateNote()
            noteRepository.getNotesByLibraryId(libraryId = 1L)
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(2)
                .onEach {
                    it.title shouldBeEqualIgnoringCase "Title"
                    it.body shouldBeEqualIgnoringCase "Body"
                }
        }

        "copy note should create a new note with a different library id" {
            noteRepository.getNotesByLibraryId(libraryId = 2)
                .first()
                .shouldBeEmpty()
            viewModel.copyNote(libraryId = 2)
            noteRepository.getNotesByLibraryId(libraryId = 2)
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
        }

        "move note should move current note to a different library" {
            noteRepository.getNotesByLibraryId(libraryId = 1)
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
            noteRepository.getNotesByLibraryId(libraryId = 2)
                .first()
                .shouldBeEmpty()
            viewModel.moveNote(libraryId = 2)
            noteRepository.getNotesByLibraryId(libraryId = 1)
                .first()
                .shouldBeEmpty()
            noteRepository.getNotesByLibraryId(libraryId = 2)
                .first()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
        }
    }
}