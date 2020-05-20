package com.noto.local

import androidx.room.*
import com.alialbaali.noto.data.source.local.LabelLocalDataSource
import com.noto.domain.model.Label

@Dao
interface LabelDao: LabelLocalDataSource {

    @Insert
    override suspend fun createLabel(label: Label)

    @Update
    override suspend fun updateLabel(label: Label)

    @Delete
    override suspend fun deleteLabel(label: Label)

    @Query("SELECT * FROM labels")
    override suspend fun getLabels(): List<Label>

    @Query("SELECT * FROM labels")
    override suspend fun getLabel(): Label

}