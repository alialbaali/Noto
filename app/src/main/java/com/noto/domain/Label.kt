package com.noto.domain

import androidx.room.*

@Entity(tableName = "labels")
data class Label(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "label_id")
    val labelId: Long = 0L,

    @ColumnInfo(name = "label_title")
    var labelTitle: String = "",

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY
)

@Entity(tableName = "noto_labels", primaryKeys = ["noto_id", "label_id"])
data class NotoLabel(
    @ColumnInfo(name = "noto_id")
    val notoId: Long,

    @ColumnInfo(name = "label_id")
    val labelId: Long
)