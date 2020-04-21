package com.noto.database

import androidx.room.*
import com.noto.domain.NoteBlock
import com.noto.domain.Noto
import com.noto.domain.TodoBlock

@Dao
interface NotoDao {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId ")
    suspend fun getNotos(libraryId: Long): List<Noto>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId LIMIT 1")
    fun getNotoById(notoId: Long): Noto

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    suspend fun countLibraryNotos(libraryId: Long): Int

    @Query("SELECT COUNT(*) FROM notos")
    suspend fun countNotos(): Int

    @Insert
    suspend fun insertAll(noto: Noto, noteBlocks: List<NoteBlock>, todoBlocks: List<TodoBlock>)

    @Update
    suspend fun updateAll(noto: Noto, noteBlocks: List<NoteBlock>, todoBlocks: List<TodoBlock>)

    @Update
    suspend fun updateNoto(noto: Noto)

}