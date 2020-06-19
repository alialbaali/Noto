package com.noto.local

import androidx.room.*
import com.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import kotlinx.coroutines.flow.Flow

@Dao
interface NotoDao : NotoLocalDataSource {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId ")
    override fun getNotos(libraryId: Long): Flow<List<Noto>>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId")
    override fun getNotoById(notoId: Long): Flow<Noto>

    @Insert
    override suspend fun createNoto(noto: Noto)

    @Delete
    override suspend fun deleteNoto(noto: Noto)

    @Update
    override suspend fun updateNoto(noto: Noto)

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    override suspend fun countLibraryNotos(libraryId: Long): Int

    @Query("SELECT COUNT(*) FROM notos")
    override suspend fun countNotos(): Int

}