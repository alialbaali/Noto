package com.noto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noto.note.model.Notebook

@Dao
interface NotebookDao {

    @Query("SELECT * FROM notebooks ORDER BY notebook_id DESC")
    fun getNotebooks(): List<Notebook>

    @Query("SELECT * FROM notebooks WHERE notebook_id = :notebookId LIMIT 1")
    fun getNotebookById(notebookId: Long): Notebook

    @Insert
    fun insertNotebook(notebook: Notebook)

    @Update
    fun updateNotebook(notebook: Notebook)

    @Delete
    fun deleteNotebook(notebook: Notebook)

}