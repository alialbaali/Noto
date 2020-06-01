package com.noto.domain.interactor.library

import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository

class GetLibraries(private val libraryRepository: LibraryRepository) {

    suspend operator fun invoke() = libraryRepository.getLibraries()

}