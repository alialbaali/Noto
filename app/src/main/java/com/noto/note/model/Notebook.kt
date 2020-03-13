package com.noto.note.model

import androidx.room.*

@Entity(tableName = "notebooks")
data class Notebook(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notebook_id")
    val notebookId: Long = 0L,

    @ColumnInfo(name = "notebook_title")
    val notebookTitle: String,

    @ColumnInfo(name = "notebook_color")
    val notebookColor: NotebookColor = NotebookColor.BLUE
)

enum class NotebookColor {
    GRAY,
    BLUE,
    PINK,
    CYAN
}

object NotebookColorConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notebookColor: NotebookColor): Int = notebookColor.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotebookColor =
        NotebookColor.values().first { notebookColor -> notebookColor.ordinal == ordinal }

}