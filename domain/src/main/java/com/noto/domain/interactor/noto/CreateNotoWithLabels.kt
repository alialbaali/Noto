package com.noto.domain.interactor.noto

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.repository.NotoRepository

class CreateNotoWithLabels(private val notoRepository: NotoRepository) {

    suspend operator fun invoke(noto: Noto, notoLabels: Set<Label>) = notoRepository.createNotoWithLabels(noto, notoLabels)

}