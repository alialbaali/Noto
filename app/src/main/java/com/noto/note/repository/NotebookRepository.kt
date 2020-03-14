package com.noto.note.repository

import androidx.lifecycle.LiveData
import com.noto.database.NotebookDao
import com.noto.note.model.Notebook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotebookRepository(private val notebookDao: NotebookDao) {

    suspend fun getNotebooks(): LiveData<List<Notebook>> {
        return withContext(Dispatchers.IO) {
            notebookDao.getNotebooks()
        }
    }

    suspend fun getNotebookById(notebookId: Long): Notebook {
        return withContext(Dispatchers.IO) {
            notebookDao.getNotebookById(notebookId)
        }
    }

    suspend fun insertNotebook(notebook: Notebook) {
        withContext(Dispatchers.IO) {
            notebookDao.insertNotebook(notebook)
        }
    }

    suspend fun updateNotebook(notebook: Notebook) {
        withContext(Dispatchers.IO) {
            notebookDao.updateNotebook(notebook)
        }
    }

    suspend fun deleteNotebook(notebook: Notebook) {
        withContext(Dispatchers.IO) {
            notebookDao.deleteNotebook(notebook)
        }
    }
}