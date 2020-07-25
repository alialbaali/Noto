package com.noto.local

import androidx.room.*
import com.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

@Dao
interface NotoDao : NotoLocalDataSource {

    @Query("SELECT * FROM notos")
    override fun getNotos(): Flow<List<Noto>>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId")
    override fun getNoto(notoId: Long): Flow<Noto>

    @Insert
    override suspend fun createNoto(noto: Noto)

    @Update
    override suspend fun updateNoto(noto: Noto)

    @Delete
    override suspend fun deleteNoto(noto: Noto)

    @Transaction
    @Query("SELECT * FROM notos WHERE noto_id = :notoId")
    override fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels>

    @Insert
    override fun createNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>)

    @Update
    override fun updateNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>)

    @Transaction
    override fun deleteNotoWithLabels(notoId: Long) {
        deleteNotoById(notoId)
        deleteNotoLabels(notoId)
    }

    @Query("DELETE FROM notos WHERE noto_id = :notoId")
    fun deleteNotoById(notoId: Long)

    @Query("DELETE FROM noto_labels WHERE noto_id = :notoId")
    fun deleteNotoLabels(notoId: Long)

}