package com.noto.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

//@Entity(tableName = "notos")
//data class Noto(
//
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "noto_id")
//    val notoId: Long = 0L,
//
//    @ColumnInfo(name = "noto_title")
//    val notoTitle: String = "",
//
//    @ColumnInfo(name = "noto_type")
//    var notoType: NotoType = NotoType.NOTEBOOK,
//
//    @ColumnInfo(name = "noto_position")
//    var notoPosition: Int,
//
//    @ColumnInfo(name = "noto_creation_date")
//    val notoCreationDate: Date = Date(),
//
//    @ColumnInfo(name = "noto_modification_date")
//    val notoModificationDate: Date = notoCreationDate,
//
//    @ColumnInfo(name = "noto_icon")
//    var notoIcon: NotoIcon = NotoIcon.NOTEBOOK,
//
//    @ColumnInfo(name = "noto_color")
//    var notoColor: NotoColor = NotoColor.GRAY,
//
//    @ColumnInfo(name = "noto_sort_type")
//    val notoSortType: SortType = SortType.DESC,
//
//    @ColumnInfo(name = "noto_sort_method")
//    val notoSortMethod: SortMethod = SortMethod.CreationDate
//)

@Entity(tableName = "notos")
data class Noto(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noto_id")
    val notoId: Long = 0L,

    @ForeignKey(
        entity = Library::class,
        parentColumns = ["library_id"],
        childColumns = ["library_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "library_id")
    var libraryId: Long,

    @ColumnInfo(name = "noto_title")
    var notoTitle: String = "",

    @ColumnInfo(name = "noto_position")
    val notoPosition: Int,

    @ColumnInfo(name = "noto_creation_date")
    val notoCreationDate: Date = Date(),

    @ColumnInfo(name = "noto_is_starred")
    var notoIsStarred: Boolean = false
)