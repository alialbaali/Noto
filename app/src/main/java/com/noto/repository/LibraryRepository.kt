package com.noto.repository

import android.content.SharedPreferences
import com.noto.database.LibraryDao
import com.noto.domain.Library
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryRepository(private val prefs: SharedPreferences, private val dao: LibraryDao) {
    suspend fun getLibraries(): List<Library> {
        return withContext(Dispatchers.IO) {
            dao.getLibraries()
        }
    }

    suspend fun insertLibrary(library: Library) {
        withContext(Dispatchers.IO) {
            dao.insertLibrary(library)
        }
    }

    suspend fun countNotos(libraryId: Long): Int{
        return withContext(Dispatchers.IO){
            dao.countNotos(libraryId)
        }
    }

    suspend fun getLibraryById(libraryId: Long): Library {
        return withContext(Dispatchers.IO){
            dao.getLibraryById(libraryId)
        }
    }
}