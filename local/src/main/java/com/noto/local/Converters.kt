package com.noto.local

import androidx.room.TypeConverter
import com.noto.domain.model.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

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

    private val formatter = DateTimeFormat.shortDate()

    @TypeConverter
    @JvmStatic
    fun toString(date: DateTime?): String? = formatter.print(date)

    @TypeConverter
    @JvmStatic
    fun toDate(value: String?): DateTime? = formatter.parseDateTime(value)
}

object SortMethodConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortMethod: SortMethod): Int = sortMethod.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortMethod = SortMethod.values().first { sortMethod -> sortMethod.ordinal == ordinal }
}

object NotoIconConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoIcon: NotoIcon): Int = notoIcon.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoIcon = NotoIcon.values().first { notoIcon -> notoIcon.ordinal == ordinal }
}
//
//object BlockConverter {
//
//    @TypeConverter
//    @JvmStatic
//    fun toOrdinal(blockType: BlockType): Int = blockType.ordinal
//
//    @TypeConverter
//    @JvmStatic
//    fun toEnum(ordinal: Int): BlockType = BlockType.values().first { blockType -> blockType.ordinal == ordinal }
//}

object StatusConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(status: Status): Int = status.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): Status = Status.values().first { status -> status.ordinal == ordinal }
}

object TypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(type: Type): Int = type.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): Type = Type.values().first { type -> type.ordinal == ordinal }
}