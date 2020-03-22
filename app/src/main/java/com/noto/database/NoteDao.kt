package com.noto.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.noto.note.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE notebookId = :notebookId")
    fun getNotes(notebookId: Long): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE note_id = :noteId")
    fun getNoteById(noteId: Long): Note

    @Insert
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

}