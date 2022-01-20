package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "libraries")
data class Library @Ignore constructor(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "parent_id", defaultValue = "NULL")
    val parentId: Long? = null,

    @Deprecated(
        message = "This shouldn't be used directly. Use library.getTitle(context) instead.",
        replaceWith = ReplaceWith("library.getTitle(context)", "import com.noto.app.util.getTitle"),
    )
    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "color")
    val color: NotoColor = NotoColor.Gray,

    @ColumnInfo(name = "creation_date")
    val creationDate: Instant = Clock.System.now(),

    @ColumnInfo(name = "layout", defaultValue = "0")
    val layout: Layout = Layout.Linear,

    @ColumnInfo(name = "note_preview_size", defaultValue = "15")
    val notePreviewSize: Int = 15,

    @ColumnInfo(name = "is_archived", defaultValue = "0")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_show_note_creation_date", defaultValue = "0")
    val isShowNoteCreationDate: Boolean = false,

    @ColumnInfo(name = "new_note_cursor_position", defaultValue = "0")
    val newNoteCursorPosition: NewNoteCursorPosition = NewNoteCursorPosition.Body,

    @ColumnInfo(name = "sorting_type", defaultValue = "1")
    val sortingType: NoteListSortingType = NoteListSortingType.CreationDate,

    @ColumnInfo(name = "sorting_order", defaultValue = "1")
    val sortingOrder: SortingOrder = SortingOrder.Descending,

    @ColumnInfo(name = "grouping", defaultValue = "0")
    val grouping: Grouping = Grouping.Default,

    @ColumnInfo(name = "is_vaulted", defaultValue = "0")
    val isVaulted: Boolean = false,

    @Ignore
    val libraries: List<Pair<Library, Int>> = emptyList(),
) {

    // Room constructor
    constructor(
        id: Long = 0L,
        parentId: Long? = null,
        title: String = "",
        position: Int,
        color: NotoColor = NotoColor.Gray,
        creationDate: Instant = Clock.System.now(),
        layout: Layout = Layout.Linear,
        notePreviewSize: Int = 15,
        isArchived: Boolean = false,
        isPinned: Boolean = false,
        isShowNoteCreationDate: Boolean = false,
        isSetNewNoteCursorOnTitle: Boolean = false,
        sortingType: NoteListSortingType = NoteListSortingType.CreationDate,
        sortingOrder: SortingOrder = SortingOrder.Descending,
        grouping: Grouping = Grouping.Default,
        isVaulted: Boolean = false,
    ) : this(
        id,
        parentId,
        title,
        position,
        color,
        creationDate,
        layout,
        notePreviewSize,
        isArchived,
        isPinned,
        isShowNoteCreationDate,
        isSetNewNoteCursorOnTitle,
        sortingType,
        sortingOrder,
        grouping,
        isVaulted,
        emptyList(),
    )

    @Suppress("FunctionName")
    companion object {
        const val InboxId = -1L
        fun Inbox() = Library(id = InboxId, position = 0, color = NotoColor.Black)
    }
}