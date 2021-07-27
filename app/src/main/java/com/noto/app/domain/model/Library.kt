package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "libraries")
data class Library(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "color")
    val color: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "icon")
    val icon: NotoIcon = NotoIcon.BOOK,

    @ColumnInfo(name = "creation_date")
    val creationDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "sorting_type")
    val sortingType: SortingType = SortingType.CreationDate,

    @ColumnInfo(name = "sorting_method")
    val sortingMethod: SortingMethod = SortingMethod.Desc

)