package com.noto.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "libraries")
data class Library(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "library_id")
    val libraryId: Long = 0L,

    @ColumnInfo(name = "library_title")
    var libraryTitle: String = "",

    @ColumnInfo(name = "library_position")
    var libraryPosition: Int,

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "noto_icon")
    var notoIcon: NotoIcon = NotoIcon.LIST,

    @ColumnInfo(name = "sort_type")
    val sortType: SortType = SortType.DESC,

    @ColumnInfo(name = "sort_method")
    val sortMethod: SortMethod = SortMethod.CreationDate,

    @ColumnInfo(name = "library_creation_date")
    val libraryCreationDate: Date = Date()
)