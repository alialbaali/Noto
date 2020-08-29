package com.noto.remote

import com.noto.data.source.remote.LibraryRemoteDataSource
import com.noto.domain.model.Library
import com.noto.domain.schema.ResponseSchema
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post


class LibraryClient(private val client: HttpClient) : LibraryRemoteDataSource {

    override suspend fun getLibraries(userToken: String): ResponseSchema<List<Library>> {

        return client.get(baseUrl) {
            authHeader(userToken)
        }

    }

    override suspend fun createLibrary(userToken: String, library: Library): ResponseSchema<Library> {

        return client.post(baseUrl) {
            authHeader(userToken)
            body = library
        }

    }

    override suspend fun updateLibrary(userToken: String, library: Library): ResponseSchema<Library> {

        return client.patch(baseUrl) {
            authHeader(userToken)
            body = library
        }

    }

    override suspend fun deleteLibrary(userToken: String, libraryId: Long) {

        return client.delete("${baseUrl}/$libraryId") {
            authHeader(userToken)
        }

    }

}