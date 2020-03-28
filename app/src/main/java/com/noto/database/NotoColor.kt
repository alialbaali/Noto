package com.noto.database

import androidx.room.TypeConverter

enum class NotoColor {
    GRAY,
    BLUE,
    PINK,
    CYAN
}

object NotoColorConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(notoColor: NotoColor): Int = notoColor.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NotoColor =
        NotoColor.values().first { notebookColor -> notebookColor.ordinal == ordinal }

}