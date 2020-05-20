package com.noto.local

import androidx.room.*
import com.alialbaali.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Noto

@Dao
interface NotoDao: NotoLocalDataSource {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId ")
    override suspend fun getNotos(libraryId: Long): List<Noto>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId LIMIT 1")
    override suspend fun getNotoById(notoId: Long): Noto

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    override suspend fun countLibraryNotos(libraryId: Long): Int

    @Query("SELECT COUNT(*) FROM notos")
    override suspend fun countNotos(): Int

    @Insert
    override suspend fun createNoto(noto: Noto)

    @Delete
    override suspend fun deleteNoto(notoId : Long)

    @Update
    override suspend fun updateNoto(noto: Noto)
}