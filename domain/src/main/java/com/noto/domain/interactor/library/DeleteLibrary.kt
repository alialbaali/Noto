package com.noto.domain.interactor.library

import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository

class DeleteLibrary(private val libraryRepository: LibraryRepository) {

    suspend operator fun invoke(library: Library) = libraryRepository.deleteLibrary(library)

}