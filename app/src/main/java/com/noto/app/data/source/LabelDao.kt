package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Label
import com.noto.app.domain.source.LabelLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao : LabelLocalDataSource {

    @Query("SELECT * FROM labels")
    override fun getLabels(): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE label_id = :labelId")
    override fun getLabel(labelId: Long): Flow<Label>

    @Insert
    override suspend fun createLabel(label: Label)

    @Update
    override suspend fun updateLabel(label: Label)

    @Delete
    override suspend fun deleteLabel(label: Label)

}