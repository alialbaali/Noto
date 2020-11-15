package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity(tableName = "notos")
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ForeignKey(
        entity = Library::class,
        parentColumns = ["library_id"],
        childColumns = ["library_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "library_id")
    val libraryId: Long,

    @ColumnInfo(name = "title")
    var title: String = String(),

    @ColumnInfo(name = "body")
    var body: String = String(),

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "is_starred")
    var isStarred: Boolean = false,

    @ColumnInfo(name = "is_archived")
    var isArchived: Boolean = false,

    @ColumnInfo(name = "reminder_date")
    var reminderDate: ZonedDateTime? = null

)