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

enum class NoteListSortingType { Manual, CreationDate, Alphabetical, }

enum class SortingOrder { Ascending, Descending, }

enum class Theme { System, SystemBlack, Light, Dark, Black, }

enum class Layout { Linear, Grid, }

enum class Font { Nunito, Monospace, }

enum class Grouping { Default, CreationDate, Label, }

enum class Language {
    System, English, Turkish, Arabic, Indonesian,

    @Deprecated("Not supported anymore.")
    Russian,

    @Deprecated("Not supported anymore.")
    Tamil,

    Spanish, French, German;

    companion object {
        @Suppress("DEPRECATION")
        val Deprecated = listOf(Tamil, Russian)
    }
}

enum class VaultTimeout { Immediately, OnAppClose, After1Hour, After4Hours, After12Hours, }

enum class NewNoteCursorPosition { Body, Title, }

enum class GroupingOrder { Ascending, Descending, }

enum class FilteringType { Inclusive, Exclusive, Strict, }

enum class OpenNotesIn { Editor, ReadingMode, }