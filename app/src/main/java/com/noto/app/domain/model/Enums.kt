package com.noto.app.domain.model

enum class NotoColor {
    Gray, Blue, Pink, Cyan, Purple, Red,
    Yellow, Orange, Green, Brown, BlueGray, Teal,
    Indigo, DeepPurple, DeepOrange, DeepGreen,
    LightBlue, LightGreen, LightRed, LightPink, Black,
}

enum class FolderListSortingType { Manual, CreationDate, Alphabetical, }

enum class NoteListSortingType { Manual, CreationDate, Alphabetical, }

enum class SortingOrder { Ascending, Descending, }

enum class Theme { System, SystemBlack, Light, Dark, Black, }

enum class Layout { Linear, Grid, }

enum class Font { Nunito, Monospace, }

enum class Grouping { Default, CreationDate, Label, }

enum class Language { System, English, Turkish, Arabic, Indonesian, Russian, Tamil, Spanish, French, }

enum class VaultTimeout { Immediately, OnAppClose, After1Hour, After4Hours, After12Hours, }

enum class NewNoteCursorPosition { Body, Title, }

enum class GroupingOrder { Ascending, Descending, }