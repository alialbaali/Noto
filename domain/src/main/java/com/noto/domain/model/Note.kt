package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity(tableName = "notes")
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
    val title: String = String(),

    @ColumnInfo(name = "body")
    val body: String = String(),

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "is_starred")
    val isStarred: Boolean = false,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "reminder_date")
    val reminderDate: ZonedDateTime? = null

)