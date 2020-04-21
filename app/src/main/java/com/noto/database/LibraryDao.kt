package com.noto.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noto.domain.Library

@Dao
interface LibraryDao {

    @Query("SELECT * FROM libraries")
    suspend fun getLibraries(): List<Library>

    @Insert
    suspend fun insertLibrary(library: Library)

    @Query("SELECT * FROM libraries WHERE library_id = :libraryId")
    suspend fun getLibraryById(libraryId: Long): Library

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    suspend fun countNotos(libraryId: Long): Int
//    @Update
//    suspend fun updateNotoList(notoList: NotoList)
//
////    @Delete
////    suspend fun deleteNotoList(notoList: NotoList)
//
//    @Update
//    suspend fun updateNotoLists(notoLists: List<NotoList>)
}