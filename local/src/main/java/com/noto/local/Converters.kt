package com.noto.local

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.domain.model.SortingMethod
import com.noto.domain.model.SortingType
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object NotoColorConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoColor: NotoColor): Int = notoColor.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoColor =
        NotoColor.values().first { notebookColor -> notebookColor.ordinal == ordinal }
}

@SuppressLint("NewApi")
object LocalDateConverter {

    private val localDateFormatter = DateTimeFormatter.ISO_DATE

    @TypeConverter
    @JvmStatic
    fun toString(localDate: LocalDate?): String? = localDate?.format(localDateFormatter)

    @TypeConverter
    @JvmStatic
    fun toDate(value: String?): LocalDate? = if (value == null) null else LocalDate.parse(value)

}

@SuppressLint("NewApi")
object ZonedDateTimeConverter {

    private val zoneDateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toString(zonedDateTime: ZonedDateTime?): String? = zonedDateTime?.format(zoneDateTimeFormatter)

    @TypeConverter
    @JvmStatic
    fun toDate(value: String?): ZonedDateTime? = if (value == null) null else ZonedDateTime.parse(value)

}

object NotoIconConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoIcon: NotoIcon): Int = notoIcon.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoIcon = NotoIcon.values().first { notoIcon -> notoIcon.ordinal == ordinal }
}

object SortingMethodConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingMethod: SortingMethod): Int = sortingMethod.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortingMethod = SortingMethod.values().first { sortingMethod -> sortingMethod.ordinal == ordinal }

}

object SortingTypeConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingType: SortingType): Int = sortingType.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortingType = SortingType.values().first { sortingType -> sortingType.ordinal == ordinal }

}