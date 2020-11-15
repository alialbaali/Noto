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
    val libraryTitle: String = "",

    @ColumnInfo(name = "library_position")
    val libraryPosition: Int,

    @ColumnInfo(name = "noto_color")
    val notoColor: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "noto_icon")
    val notoIcon: NotoIcon = NotoIcon.BOOK,

    @ColumnInfo(name = "library_creation_date")
    val libraryCreationDate: LocalDate = LocalDate.now()

)