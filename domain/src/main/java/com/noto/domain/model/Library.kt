package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

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
    var notoIcon: NotoIcon = NotoIcon.BOOK,

    @ColumnInfo(name = "sort_type")
    var sortType: SortType = SortType.DESC,

    @ColumnInfo(name = "sort_method")
    var sortMethod: SortMethod = SortMethod.CreationDate,

    @ColumnInfo(name = "library_creation_date")
    val libraryCreationDate: LocalDate = LocalDate.now()
)