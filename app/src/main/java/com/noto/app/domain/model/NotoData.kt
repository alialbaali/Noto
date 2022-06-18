package com.noto.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotoData(
    val folders: List<Folder> = emptyList(),
    val notes: List<Note> = emptyList(),
    val labels: List<Label> = emptyList(),
    val noteLabels: List<NoteLabel> = emptyList(),
    val settings: SettingsConfig = SettingsConfig(),
)