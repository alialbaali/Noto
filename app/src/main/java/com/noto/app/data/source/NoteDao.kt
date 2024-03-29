package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.FolderIdWithNotesCount
import com.noto.app.domain.model.Note
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao : LocalNoteDataSource {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    override fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE is_archived = 0 ORDER BY id DESC")
    override fun getAllMainNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE folder_id = :folderId AND is_archived = 0 ORDER BY id DESC")
    override fun getNotesByFolderId(folderId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE folder_id = :folderId AND is_archived = 1 ORDER BY id DESC")
    override fun getArchivedNotesByFolderId(folderId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    override fun getNoteById(noteId: Long): Flow<Note>

    @Query("SELECT folder_id, COUNT(*) as notesCount FROM notes WHERE is_archived = 0 GROUP BY folder_id")
    override fun getFoldersNotesCount(): Flow<List<FolderIdWithNotesCount>>

    @Insert
    override suspend fun createNote(note: Note): Long

    @Update
    override suspend fun updateNote(note: Note)

    @Delete
    override suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes")
    override suspend fun clearNotes()
}