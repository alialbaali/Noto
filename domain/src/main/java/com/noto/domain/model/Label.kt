package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class Label(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "label_id")
    val labelId: Long = 0,

    @ColumnInfo(name = "label_title")
    val labelTitle: String,

    @ColumnInfo(name = "label_noto_color")
    val labelColor: NotoColor

)