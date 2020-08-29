package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entities_status")
data class EntityStatus(

    @PrimaryKey(autoGenerate = true)
    val entityStatusId: Long = 0L,

    @ColumnInfo(name = "library_id")
    val libraryId: Long,

    @ColumnInfo(name = "noto_id")
    val notoId: Long? = null,

    @ColumnInfo(name = "type")
    val type: Type,

    @ColumnInfo(name = "status")
    val status: Status

)