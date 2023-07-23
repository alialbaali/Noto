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
    fun toEnum(ordinal: Int): NotoColor = NotoColor.entries.first { notebookColor -> notebookColor.ordinal == ordinal }
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
    fun toEnum(ordinal: Int): Layout = Layout.entries.first { layoutManager -> layoutManager.ordinal == ordinal }

}

object SortingTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingType: NoteListSortingType): Int = sortingType.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NoteListSortingType = NoteListSortingType.entries.first { sortingType -> sortingType.ordinal == ordinal }

}

object SortingOrderConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingOrder: SortingOrder): Int = sortingOrder.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortingOrder = SortingOrder.entries.first { sortingOrder -> sortingOrder.ordinal == ordinal }

}

object GroupingConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(grouping: Grouping): Int = grouping.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): Grouping = Grouping.entries.first { grouping -> grouping.ordinal == ordinal }

}

object NewNoteCursorPositionConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(position: NewNoteCursorPosition): Int = position.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NewNoteCursorPosition = NewNoteCursorPosition.entries.first { position -> position.ordinal == ordinal }

}

object GroupingOrderConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(groupingOrder: GroupingOrder): Int = groupingOrder.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): GroupingOrder = GroupingOrder.entries.first { groupingOrder -> groupingOrder.ordinal == ordinal }

}

object FilteringTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(filteringType: FilteringType): Int = filteringType.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): FilteringType = FilteringType.entries.first { filteringType -> filteringType.ordinal == ordinal }

}

object OpenNotesInConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(openNotesIn: OpenNotesIn): Int = openNotesIn.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): OpenNotesIn = OpenNotesIn.entries.first { openNotesIn -> openNotesIn.ordinal == ordinal }

}