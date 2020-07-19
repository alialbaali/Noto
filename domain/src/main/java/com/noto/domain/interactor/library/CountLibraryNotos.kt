package com.noto.domain.interactor.library

import com.noto.domain.repository.LibraryRepository

class CountLibraryNotos(private val libraryRepository: LibraryRepository) {

    suspend operator fun invoke(libraryId: Long) = libraryRepository.countLibraryNotos(libraryId)

}
