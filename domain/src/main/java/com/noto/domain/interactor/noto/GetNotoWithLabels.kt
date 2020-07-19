package com.noto.domain.interactor.noto

import com.noto.domain.repository.NotoRepository

class GetNotoWithLabels(private val notoRepository: NotoRepository) {

    suspend operator fun invoke(notoId: Long) = notoRepository.getNotoWithLabels(notoId)

}