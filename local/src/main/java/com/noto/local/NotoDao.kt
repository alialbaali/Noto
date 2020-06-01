package com.noto.local

import androidx.room.*
import com.alialbaali.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotoDao : NotoLocalDataSource {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId ")
    override fun getNotos(libraryId: Long): Flow<List<Noto>>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId")
    override fun getNotoById(notoId: Long): Flow<Noto>

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    override suspend fun countLibraryNotos(libraryId: Long): Int

    @Query("SELECT COUNT(*) FROM notos")
    override suspend fun countNotos(): Int

    @Insert
    override suspend fun createNoto(noto: Noto, notoLabels: List<NotoLabel>)

    @Delete
    override suspend fun deleteNoto(noto: Noto)

//    @Query("De")
//    override suspend fun deleteNoto(notoId: Long, notoLabels: List<NotoLabel>)

    @Update
    override suspend fun updateNoto(noto: Noto, notoLabels: List<NotoLabel>)

    @Query("SELECT * FROM noto_labels WHERE noto_id =:notoId")
    override suspend fun getNotoLabels(notoId: Long): List<NotoLabel>

    @Query("SELECT * FROM labels")
    override suspend fun getLabels(): List<Label>
}