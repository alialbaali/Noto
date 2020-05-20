package com.noto.domain.interactor.noto

import com.noto.domain.model.NotoLabel
import com.noto.domain.repository.NotoRepository

class InsertNotoLabels(private val notoRepository: NotoRepository) {

   suspend operator fun invoke(notoLabels: List<NotoLabel>) = notoRepository.insertNotoLabels(notoLabels)

}
