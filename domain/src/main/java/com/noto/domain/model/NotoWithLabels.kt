package com.noto.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NotoWithLabels(

    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id",
        entityColumn = "label_id",
        associateBy = Junction(NotoLabel::class)
    )
    val labels: Set<Label>

)