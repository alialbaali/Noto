package com.noto.domain.interactor.label

import com.noto.domain.model.Label
import com.noto.domain.repository.LabelRepository

class CreateLabel(private val labelRepository: LabelRepository) {

    suspend operator fun invoke(label: Label) = labelRepository.createLabel(label)

}