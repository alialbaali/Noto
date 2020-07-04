package com.noto.domain.interactor.noto

import com.noto.domain.repository.NotoRepository

class GetArchivedNotos(private val notoRepository: NotoRepository) {

    suspend operator fun invoke() = notoRepository.getArchivedNotos()

}