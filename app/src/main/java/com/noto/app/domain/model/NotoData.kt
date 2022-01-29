package com.noto.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotoData(
    val folders: List<Folder>,
    val notes: List<Note>,
    val labels: List<Label>,
    val noteLabels: List<NoteLabel>,
    val settings: SettingsConfig,
)