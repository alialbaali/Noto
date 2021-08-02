package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteLabel
import com.noto.app.domain.model.NoteWithLabels
import com.noto.app.domain.source.NoteLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao : NoteLocalDataSource {

    @Query("SELECT * FROM notes WHERE library_id = :libraryId AND is_archived = 0 ORDER BY id DESC")
    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE library_id = :libraryId AND is_archived = 1")
    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    override fun getNoteById(noteId: Long): Flow<Note>

    @Insert
    override suspend fun createNote(note: Note)

    @Update
    override suspend fun updateNote(note: Note)

    @Delete
    override suspend fun deleteNote(note: Note)

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :notoId")
    override fun getNoteWithLabels(notoId: Long): Flow<NoteWithLabels>

    @Insert
    override fun createNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>)

    @Update
    override fun updateNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>)

    @Transaction
    override fun deleteNoteWithLabels(notoId: Long) {
        deleteNoteById(notoId)
        deleteNoteLabels(notoId)
    }

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Long)

    @Query("DELETE FROM noto_labels WHERE id = :noteId")
    fun deleteNoteLabels(noteId: Long)

}