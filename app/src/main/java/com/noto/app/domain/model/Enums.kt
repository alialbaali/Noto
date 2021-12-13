package com.noto.app.domain.model

enum class NotoColor {
    Gray, Blue, Pink, Cyan, Purple, Red,
    Yellow, Orange, Green, Brown, BlueGray, Teal,
    Indigo, DeepPurple, DeepOrange, DeepGreen,
    LightBlue, LightGreen, LightRed, LightPink, Black,
}

enum class LibraryListSortingType { Manual, CreationDate, Alphabetical, }

enum class NoteListSortingType { Manual, CreationDate, Alphabetical, }

enum class SortingOrder { Ascending, Descending, }

enum class Theme { System, SystemBlack, Light, Dark, Black, }

enum class Layout { Linear, Grid, }

enum class Font { Nunito, Monospace, }

enum class Grouping { Default, CreationDate, Label, }

enum class Language { System, English, Turkish, Arabic, Indonesian, Russian, Tamil, }