package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Label
import com.noto.app.domain.source.LocalLabelDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao : LocalLabelDataSource {

    @Query("SELECT * FROM labels")
    override fun getAllLabels(): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE folder_id = :folderId")
    override fun getLabelsByFolderId(folderId: Long): Flow<List<Label>>

    @Query("SELECT * FROM labels WHERE id = :id")
    override fun getLabelById(id: Long): Flow<Label>

    @Insert
    override suspend fun createLabel(label: Label): Long

    @Update
    override suspend fun updateLabel(label: Label)

    @Delete
    override suspend fun deleteLabel(label: Label)
}