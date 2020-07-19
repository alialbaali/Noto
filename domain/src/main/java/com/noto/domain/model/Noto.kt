package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZonedDateTime

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
    val libraryId: Long,

    @ColumnInfo(name = "noto_title")
    var notoTitle: String = String(),

    @ColumnInfo(name = "noto_body")
    var notoBody: String = String(),

    @ColumnInfo(name = "noto_position")
    val notoPosition: Int,

    @ColumnInfo(name = "noto_creation_date")
    val notoCreationDate: LocalDate = LocalDate.now(),

    @ColumnInfo(name = "noto_is_starred")
    var notoIsStarred: Boolean = false,

    @ColumnInfo(name = "noto_is_archived")
    var notoIsArchived: Boolean = false,

    @ColumnInfo(name = "noto_reminder")
    var notoReminder: ZonedDateTime? = null,

    @ColumnInfo(name = "noto_is_completed")
    val notoIsCompleted: Boolean = false

)