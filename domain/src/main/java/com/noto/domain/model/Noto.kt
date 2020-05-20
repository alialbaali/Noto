package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

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


    @ColumnInfo(name = "noto_body")
    var notoBody: String = "",

    @ColumnInfo(name = "noto_position")
    var notoPosition: Int,

    @ColumnInfo(name = "noto_creation_date")
    val notoCreationDate: DateTime = DateTime.now(DateTimeZone.getDefault()),

    @ColumnInfo(name = "noto_is_starred")
    var notoIsStarred: Boolean = false,

    @ColumnInfo(name = "noto_schedule")
    val notoReminder: DateTime? = null,

    @ColumnInfo(name = "noto_is_completed")
    val notoIsCompleted: Boolean = false
)