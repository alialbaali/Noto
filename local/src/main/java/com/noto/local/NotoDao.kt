package com.noto.local

import androidx.room.*
import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Note
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

@Dao
interface NotoDao : NotoLocalDataSource {

    @Query("SELECT * FROM notos WHERE library_id = :libraryId AND is_archived = 0")
    override fun getNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notos WHERE library_id = :libraryId AND is_archived = 1")
    override fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notos WHERE id = :notoId")
    override fun getNotoById(notoId: Long): Flow<Note>

    @Insert
    override suspend fun createNoto(note: Note)

    @Update
    override suspend fun updateNoto(note: Note)

    @Delete
    override suspend fun deleteNoto(note: Note)

    @Transaction
    @Query("SELECT * FROM notos WHERE id = :notoId")
    override fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels>

    @Insert
    override fun createNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>)

    @Update
    override fun updateNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>)

    @Transaction
    override fun deleteNotoWithLabels(notoId: Long) {
        deleteNotoById(notoId)
        deleteNotoLabels(notoId)
    }

    @Query("DELETE FROM notos WHERE id = :notoId")
    fun deleteNotoById(notoId: Long)

    @Query("DELETE FROM noto_labels WHERE id = :notoId")
    fun deleteNotoLabels(notoId: Long)

}