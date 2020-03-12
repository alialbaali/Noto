package com.noto.database

import androidx.room.*
import com.noto.note.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY note_id DESC")
    fun getNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE note_id = :noteId LIMIT 1")
    fun getNoteById(noteId: Long): Note

    @Insert
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}