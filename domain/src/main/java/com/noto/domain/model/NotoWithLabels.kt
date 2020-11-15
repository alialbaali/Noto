package com.noto.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithLabels(

    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id",
        entityColumn = "label_id",
        associateBy = Junction(NoteLabel::class)
    )
    val labels: Set<Label>

)