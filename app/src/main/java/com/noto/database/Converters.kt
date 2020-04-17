package com.noto.database

import androidx.room.TypeConverter
import java.text.DateFormat
import java.util.*


object NotoColorConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoColor: NotoColor): Int = notoColor.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoColor =
        NotoColor.values().first { notebookColor -> notebookColor.ordinal == ordinal }

}

object SortTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortType: SortType): Int = sortType.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortType = SortType.values().first { sort -> sort.ordinal == ordinal }

}

object DateConverter {

    private const val dateFormat = DateFormat.FULL

    @TypeConverter
    @JvmStatic
    fun toString(date: Date): String =
        DateFormat.getDateTimeInstance(dateFormat, dateFormat).format(date)

    @TypeConverter
    @JvmStatic
    fun toDate(value: String): Date =
        DateFormat.getDateTimeInstance(dateFormat, dateFormat).parse(value) ?: Date()

}

object SortMethodConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortMethod: SortMethod): Int = sortMethod.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortMethod =
        SortMethod.values().first { sortMethod -> sortMethod.ordinal == ordinal }

}

object NotoIconConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoIcon: NotoIcon): Int = notoIcon.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoIcon = NotoIcon.values().first { notoIcon -> notoIcon.ordinal == ordinal }
}