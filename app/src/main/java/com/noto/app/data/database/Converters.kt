package com.noto.app.data.database

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.noto.app.domain.model.*
import kotlinx.datetime.Instant

object NotoColorConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoColor: NotoColor): Int = notoColor.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoColor = NotoColor.values().first { notebookColor -> notebookColor.ordinal == ordinal }
}

@SuppressLint("NewApi")
object InstantConverter {

    @TypeConverter
    @JvmStatic
    fun toString(instant: Instant?): String? = instant?.toString()

    @TypeConverter
    @JvmStatic
    fun toDate(value: String?): Instant? = value?.let { Instant.parse(it) }

}

object LayoutConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(layout: Layout): Int = layout.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): Layout = Layout.values().first { layoutManager -> layoutManager.ordinal == ordinal }

}

object SortingTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingType: NoteListSortingType): Int = sortingType.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NoteListSortingType = NoteListSortingType.values().first { sortingType -> sortingType.ordinal == ordinal }

}

object SortingOrderConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingOrder: SortingOrder): Int = sortingOrder.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortingOrder = SortingOrder.values().first { sortingOrder -> sortingOrder.ordinal == ordinal }

}

object GroupingConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(grouping: Grouping): Int = grouping.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): Grouping = Grouping.values().first { grouping -> grouping.ordinal == ordinal }

}

object NewNoteCursorPositionConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(position: NewNoteCursorPosition): Int = position.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NewNoteCursorPosition = NewNoteCursorPosition.values().first { position -> position.ordinal == ordinal }

}