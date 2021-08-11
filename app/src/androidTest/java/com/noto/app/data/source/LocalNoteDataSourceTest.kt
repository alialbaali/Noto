package com.noto.app.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.noto.app.di.localDataSourceModule
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.source.LocalLibraryDataSource
import com.noto.app.domain.source.LocalNoteDataSource
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
class LocalNoteDataSourceTest : KoinTest {

    private lateinit var source: LocalNoteDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        unloadKoinModules(localDataSourceModule)
        loadKoinModules(inMemoryDatabaseModule)
        source = get()
        runBlocking {
            source.clearNotes()
            get<LocalLibraryDataSource>().apply {
                clearLibraries()
                createLibrary(Library(id = 1, title = "Work", position = 0))
            }
        }
    }

    @Test
    fun get_notes_by_library_id_should_return_an_empty_list() = runBlockingTest {
        val dbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { dbNotes.isEmpty() }
    }

    @Test
    fun get_archived_notes_by_library_id_should_return_an_empty_list() = runBlockingTest {
        val dbArchivedNotes = source.getArchivedNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { dbArchivedNotes.isEmpty() }
    }

    @Test
    fun create_archived_note_should_insert_new_archived_note_in_the_database() = runBlockingTest {
        repeat(5) {
            val note = createNote(isArchived = it % 2 == 0)
            source.createNote(note)
        }

        val dbArchivedNotes = source.getArchivedNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { dbArchivedNotes.count() == 3 }
    }

    @Test
    fun create_new_note_should_insert_new_note_in_the_database() = runBlockingTest {
        val note = createNote()
        source.createNote(note)

        val dbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertContains(dbNotes, note.copy(id = 18))
    }

    @Test
    fun update_note_should_update_note_with_matching_id() = runBlockingTest {
        val note = createNote()
        source.createNote(note)

        val dbNote = source.getNotesByLibraryId(libraryId = 1)
            .first()
            .first()

        val updatedNote = note.copy(id = dbNote.id, title = "Code")
        source.updateNote(updatedNote)

        val updatedDbNote = source.getNoteById(noteId = dbNote.id)
            .first()

        assertEquals(updatedNote, updatedDbNote)
    }

    @Test
    fun delete_note_should_delete_note_with_matching_id() = runBlockingTest {
        val note = createNote()
        source.createNote(note)

        val dbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertContains(dbNotes, note.copy(id = 6))

        source.deleteNote(note.copy(id = 6))

        val updatedDbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { updatedDbNotes.isEmpty() }
    }

    @Test
    fun count_notes_should_count_all_notes_with_matching_library_id() = runBlockingTest {
        repeat(5) {
            val note = createNote()
            source.createNote(note)
        }

        val dbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { dbNotes.count() == 5 }
    }

    @Test
    fun clear_notes_should_remove_all_notes() = runBlockingTest {
        repeat(5) {
            val note = createNote()
            source.createNote(note)
        }

        val dbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { dbNotes.count() == 5 }

        source.clearNotes()

        val updatedDbNotes = source.getNotesByLibraryId(libraryId = 1)
            .first()

        assertTrue { updatedDbNotes.isEmpty() }
    }

    private fun createNote(libraryId: Long = 1, title: String = "Work", body: String = "Working", isArchived: Boolean = false) =
        Note(libraryId = libraryId, title = title, body = body, position = 0, isArchived = isArchived)
}