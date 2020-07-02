package com.noto.domain.interactor.label

import com.noto.domain.repository.LabelRepository

class GetLabels(private val labelRepository: LabelRepository) {

    suspend operator fun invoke() = labelRepository.getLabels()

}