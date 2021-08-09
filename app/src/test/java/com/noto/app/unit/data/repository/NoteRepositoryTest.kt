package com.noto.app.unit.data.repository

import com.noto.app.di.repositoryModule
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.fakeLocalDataSourceModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import kotlinx.coroutines.flow.single
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class NoteRepositoryTest : StringSpec(), KoinTest {

    private lateinit var repository: NoteRepository

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

        "get all unarchived notes should be empty" {
            repository.getNotesByLibraryId(0)
                .single()
                .shouldBeEmpty()
        }

        "create note should insert a new note" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0)

            repository.createNote(note)
            repository.getNotesByLibraryId(libraryId = 1)
                .single()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
                .shouldContain(note)
        }

        "get note by id should return an existing note with matching id" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0)
            repository.createNote(note)

            repository.getNoteById(noteId = 1)
                .single()
                .apply {
                    libraryId shouldBeExactly 1
                    title shouldBeEqualIgnoringCase "Title"
                    body shouldBeEqualIgnoringCase "Body"
                }
        }

        "get all archived notes should be empty" {
            repository.getArchivedNotesByLibraryId(0)
                .single()
                .shouldBeEmpty()
        }

        "create archived note should insert a new archived note" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0, isArchived = true)
            repository.createNote(note)
            repository.getArchivedNotesByLibraryId(libraryId = 1)
                .single()
                .shouldNotBeEmpty()
                .shouldHaveSize(1)
                .shouldContain(note)
        }

        "get archived note by id should return a note with matching id" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0, isArchived = true)
            repository.createNote(note)

            repository.getNoteById(noteId = 1)
                .single()
                .isArchived shouldBe true
        }

        "update note by id should update an existing note with matching id" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0, isArchived = true)
            repository.createNote(note)

            val updateNote = note.copy(title = "Title1", body = "Body1")
            repository.updateNote(updateNote)

            repository.getNoteById(noteId = 1)
                .single()
                .apply {
                    title shouldBeEqualIgnoringCase "Title1"
                    body shouldBeEqualIgnoringCase "Body1"
                }
        }

        "delete note by id should remove an existing note with matching id" {
            val note = Note(id = 1, libraryId = 1, title = "Title", body = "Body", position = 0)
            repository.createNote(note)

            repository.getNotesByLibraryId(libraryId = 1)
                .single()
                .shouldNotBeEmpty()

            repository.deleteNote(note)

            repository.getNotesByLibraryId(libraryId = 1)
                .single()
                .shouldBeEmpty()
        }

        "count library notes should return count of all notes with matching library id" {
            repeat(5) {
                val note = Note(id = it.toLong(), libraryId = 1, title = "Title $it", "Body $it", position = 0)
                repository.createNote(note)
            }
            repository.countLibraryNotes(libraryId = 1) shouldBeExactly 5
        }
    }
}