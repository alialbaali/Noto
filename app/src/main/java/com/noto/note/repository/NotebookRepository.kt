package com.noto.note.repository

import android.content.SharedPreferences
import com.noto.database.NotebookDao
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.note.model.Notebook
import com.noto.util.getValue
import com.noto.util.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val NOTEBOOK_LIST_SORT_TYPE_KEY = "notebook_list_sort_type"
private const val NOTEBOOK_LIST_SORT_METHOD_KEY = "notebook_list_sort_method"

class NotebookRepository(private val sharedPreferences: SharedPreferences, private val notebookDao: NotebookDao) {

    suspend fun getNotebooks(): List<Notebook> {
        return withContext(Dispatchers.IO) {
            notebookDao.getNotebooks()
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

    suspend fun deleteNotebook(notebookId: Long) {
        withContext(Dispatchers.IO) {
            notebookDao.deleteNotebook(notebookId)
        }
    }

    suspend fun swapNotebooks(from: Notebook, to: Notebook) {
        withContext(Dispatchers.IO) {
            notebookDao.swapNotebooks(from, to)
        }
    }

    suspend fun updateNotebooks(notebooks: List<Notebook>) {
        withContext(Dispatchers.IO) {
            notebookDao.updateNotebooks(notebooks)
        }
    }

    fun updateSortType(sortType: SortType) {
        sharedPreferences.setValue(NOTEBOOK_LIST_SORT_TYPE_KEY, sortType.name)
    }

    fun updateSortMethod(sortMethod: SortMethod) {
        sharedPreferences.setValue(NOTEBOOK_LIST_SORT_METHOD_KEY, sortMethod.name)
    }

    fun getSortType(): SortType {
        var value = sharedPreferences.getValue(NOTEBOOK_LIST_SORT_TYPE_KEY) as String?

        if (value.isNullOrBlank()) {
            updateSortType(SortType.ASC)
            value = SortType.ASC.name
        }

        return SortType.valueOf(value)
    }

    fun getSortMethod(): SortMethod {
        var value = sharedPreferences.getValue(NOTEBOOK_LIST_SORT_METHOD_KEY) as String?

        if (value.isNullOrBlank()) {
            updateSortMethod(SortMethod.CreationDate)
            value = SortMethod.CreationDate.name
        }

        return SortMethod.valueOf(value)
    }
}