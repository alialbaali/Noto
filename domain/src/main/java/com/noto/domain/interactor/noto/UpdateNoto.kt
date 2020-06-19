package com.noto.domain.interactor.noto

import com.noto.domain.model.Noto
import com.noto.domain.repository.NotoRepository

class UpdateNoto(private val notoRepository: NotoRepository) {

    suspend operator fun invoke(noto: Noto) = notoRepository.updateNoto(noto)

}