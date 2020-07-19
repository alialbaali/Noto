package com.noto.domain.interactor.library

import com.noto.domain.repository.LibraryRepository

class CountLibraries(private val libraryRepository: LibraryRepository) {

    suspend operator fun invoke() = libraryRepository.countLibraries()

}