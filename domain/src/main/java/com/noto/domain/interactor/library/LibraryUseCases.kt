package com.noto.domain.interactor.library

class LibraryUseCases(
    val createLibrary: CreateLibrary,
    val deleteLibrary: DeleteLibrary,
    val updateLibrary: UpdateLibrary,
    val getLibraries: GetLibraries,
    val getLibraryById: GetLibraryById,
    val countLibraryNotos: CountLibraryNotos,
    val countLibraries: CountLibraries
)