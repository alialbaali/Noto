package com.noto.domain.interactor.noto

import com.noto.domain.repository.NotoRepository

class GetAllNotos(private val notoRepository: NotoRepository) {

    suspend operator fun invoke() = notoRepository.getAllNotos()

}
