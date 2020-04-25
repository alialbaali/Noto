package com.noto.database

import androidx.room.*
import com.noto.domain.Label

@Dao
interface LabelDao {

    @Insert
    suspend fun insertLabel(label: Label)

    @Update
    suspend fun updateLabel(label: Label)

    @Delete
    suspend fun deleteLabel(label: Label)

    @Query("SELECT * FROM labels")
    suspend fun getLabels(): List<Label>

}