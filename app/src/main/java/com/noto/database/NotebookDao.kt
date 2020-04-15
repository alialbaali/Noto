package com.noto.database

import androidx.room.*
import com.noto.note.model.Notebook

@Dao
interface NotebookDao {

    @Query("SELECT * FROM notebooks")
    suspend fun getNotebooks(): List<Notebook>

    @Query("SELECT * FROM notebooks WHERE notebook_id = :notebookId LIMIT 1")
    suspend fun getNotebookById(notebookId: Long): Notebook

    @Insert
    suspend fun insertNotebook(notebook: Notebook)

    @Update
    suspend fun updateNotebook(notebook: Notebook)

    @Transaction
    @Query("DELETE FROM notebooks WHERE notebook_id = :notebookId")
    suspend fun deleteNotebook(notebookId: Long)

    @Update
    suspend fun swapNotebooks(from: Notebook, to: Notebook)

    @Update
    suspend fun updateNotebooks(notebooks: List<Notebook>)
}