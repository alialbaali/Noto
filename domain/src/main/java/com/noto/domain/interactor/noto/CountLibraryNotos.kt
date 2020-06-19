package com.noto.domain.interactor.noto

import com.noto.domain.repository.NotoRepository

class CountLibraryNotos(private val notoRepository: NotoRepository) {

    suspend operator fun invoke(libraryId: Long) = notoRepository.countLibraryNotos(libraryId)

}