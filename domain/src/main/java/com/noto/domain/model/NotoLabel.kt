package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noto_labels")
data class NotoLabel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noto_label_id")
    val NotoLabelId: Long = 0,

    @ColumnInfo(name = "noto_id")
    val notoId: Long,

    @ColumnInfo(name = "label_id")
    val labelId: Long

)

fun NotoLabel.toLabel(labelTitle: String, labelColor: NotoColor) = Label(labelId, labelTitle, labelColor)

fun Label.toNotoLabel(notoId: Long) = NotoLabel(labelId = labelId, notoId =  notoId)