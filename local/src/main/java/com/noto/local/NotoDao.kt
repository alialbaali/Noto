package com.noto.local

import androidx.room.*
import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

@Dao
interface NotoDao : NotoLocalDataSource {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId AND noto_is_archived = 0")
    override fun getNotosByLibraryId(libraryId: Long): Flow<List<Noto>>

    @Query("SELECT * FROM notos WHERE library_id = :libraryId AND noto_is_archived = 1")
    override fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Noto>>

    @Query("SELECT * FROM notos WHERE noto_id = :notoId")
    override fun getNotoById(notoId: Long): Flow<Noto>

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