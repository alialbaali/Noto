package com.noto.domain.interactor.label

class LabelUseCases(
    val createLabel: CreateLabel,
    val deleteLabel: DeleteLabel,
    val updateLabel: UpdateLabel,
    val getLabel: GetLabel,
    val getLabels: GetLabels
)