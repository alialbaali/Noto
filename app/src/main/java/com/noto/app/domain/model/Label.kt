package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "labels",
    foreignKeys = [ForeignKey(
        entity = Library::class,
        parentColumns = ["id"],
        childColumns = ["library_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Label(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "library_id")
    val libraryId: Long,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "color")
    val color: NotoColor = NotoColor.Gray,

    @ColumnInfo(name = "position", defaultValue = "0")
    val position: Int = 0,
)