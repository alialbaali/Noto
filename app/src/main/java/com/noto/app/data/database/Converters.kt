package com.noto.app.data.database

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.noto.app.domain.model.LayoutManager
import com.noto.app.domain.model.NoteListSorting
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.SortingOrder
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

object LayoutManagerConvertor {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(layoutManager: LayoutManager): Int = layoutManager.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): LayoutManager = LayoutManager.values().first { layoutManager -> layoutManager.ordinal == ordinal }

}

object SortingConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sorting: NoteListSorting): Int = sorting.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): NoteListSorting = NoteListSorting.values().first { sorting -> sorting.ordinal == ordinal }

}

object SortingOrderConverter {

    @TypeConverter
    @JvmStatic
    fun toOrdinal(sortingOrder: SortingOrder): Int = sortingOrder.ordinal

    @TypeConverter
    @JvmStatic
    fun toEnum(ordinal: Int): SortingOrder = SortingOrder.values().first { sortingOrder -> sortingOrder.ordinal == ordinal }

}