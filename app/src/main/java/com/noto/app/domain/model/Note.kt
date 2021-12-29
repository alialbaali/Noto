package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Library::class,
        parentColumns = ["id"],
        childColumns = ["library_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "library_id")
    val libraryId: Long,

    @ColumnInfo(name = "title")
    val title: String = String(),

    @ColumnInfo(name = "body")
    val body: String = String(),

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: Instant = Clock.System.now(),

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "reminder_date")
    val reminderDate: Instant? = null,

    @ColumnInfo(name = "is_vaulted", defaultValue = "0")
    val isVaulted: Boolean = false,
)