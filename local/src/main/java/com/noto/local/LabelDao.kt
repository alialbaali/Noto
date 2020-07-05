package com.noto.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.noto.data.source.local.LabelLocalDataSource
import com.noto.domain.model.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao : LabelLocalDataSource {

    @Insert
    override suspend fun createLabel(label: Label)

    @Update
    override suspend fun updateLabel(label: Label)

    @Query("DELETE FROM labels WHERE label_id = :labelId")
    override suspend fun deleteLabel(labelId: Long)

    @Query("SELECT * FROM labels")
    override fun getLabels(): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE label_id = :labelId")
    override fun getLabelById(labelId: Long): Flow<Label>

}