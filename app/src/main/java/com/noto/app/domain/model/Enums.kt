package com.noto.app.domain.model

enum class NotoColor {
    Gray, Blue, Pink, Cyan, Purple, Red,
    Yellow, Orange, Green, Brown, BlueGray, Teal,
    Indigo, DeepPurple, DeepOrange, DeepGreen,
    LightBlue, LightGreen, LightRed, LightPink, Black,
}

enum class Icon {
    Futuristic, DarkRain, Airplane, BlossomIce, DarkAlpine,
    DarkSide, Earth, Fire, Purpleberry, SanguineSun,
}

enum class FolderListSortingType { Manual, CreationDate, Alphabetical, }

enum class NoteListSortingType { Manual, CreationDate, Alphabetical, AccessDate, }

enum class SortingOrder { Ascending, Descending, }

enum class Theme { System, SystemBlack, Light, Dark, Black, }

enum class Layout { Linear, Grid, }

enum class Font { Nunito, Monospace, }

enum class Grouping { None, CreationDate, Label, AccessDate }

enum class Language {
    System, English, Turkish, Arabic, Indonesian, Russian,

    @Deprecated("Not supported anymore.")
    Tamil,

    Spanish, French, German, Italian, Czech,
    Lithuanian, SimplifiedChinese, Portuguese, Korean;

    companion object {
        @Suppress("DEPRECATION")
        val Deprecated = listOf(Tamil)
    }

    val isSingleForm: Boolean get() = this == Indonesian || this == SimplifiedChinese || this == Korean
}

enum class VaultTimeout { Immediately, OnAppClose, After1Hour, After4Hours, After12Hours, }

enum class NewNoteCursorPosition { Body, Title, }

enum class GroupingOrder { Ascending, Descending, }

enum class FilteringType { Inclusive, Exclusive, Strict, }

enum class OpenNotesIn { Editor, ReadingMode, }

enum class ScreenBrightnessLevel(val value: Float) {
    System(-1F), Min(0F), VeryLow(0.10F),
    Low(0.25F), Medium(0.50F), High(0.75F),
    VeryHigh(0.90F), Max(1F),
}