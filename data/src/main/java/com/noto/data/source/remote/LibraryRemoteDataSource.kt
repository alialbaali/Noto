package com.noto.data.source.remote

import com.noto.domain.model.Library
import com.noto.domain.schema.ResponseSchema

interface LibraryRemoteDataSource {

    val baseUrl : String
        get() = "/libraries"

    suspend fun getLibraries(userToken: String): ResponseSchema<List<Library>>

    suspend fun createLibrary(userToken: String, library: Library): ResponseSchema<Library>

    suspend fun updateLibrary(userToken: String, library: Library): ResponseSchema<Library>

    suspend fun deleteLibrary(userToken: String, libraryId: Long): ResponseSchema<Nothing>

}