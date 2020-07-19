package com.noto.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel

data class NotoWithLabels(

    @Embedded
    val noto: Noto,

    @Relation(
        parentColumn = "noto_id",
        entityColumn = "label_id",
        associateBy = Junction(NotoLabel::class)
    )
    val labels: Set<Label>

)