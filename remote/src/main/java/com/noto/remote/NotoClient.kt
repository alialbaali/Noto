package com.noto.remote

import com.noto.data.source.remote.NotoRemoteDataSource
import com.noto.domain.model.Noto
import com.noto.domain.schema.ResponseSchema
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post

class NotoClient(private val client: HttpClient) : NotoRemoteDataSource {

    override suspend fun getNotos(userToken: String): ResponseSchema<List<Noto>> {

        return client.get(baseUrl) {
            authHeader(userToken)
        }

    }

    override suspend fun createNoto(userToken: String, noto: Noto): ResponseSchema<Noto> {

        return client.post(baseUrl) {
            authHeader(userToken)
            body = noto
        }

    }

    override suspend fun updateNoto(userToken: String, noto: Noto): ResponseSchema<Noto> {

        return client.patch(baseUrl) {
            authHeader(userToken)
            body = noto
        }

    }

    override suspend fun deleteNoto(userToken: String, libraryId: Long, notoId: Long): ResponseSchema<Nothing> {

        return client.delete("$baseUrl/$libraryId/$notoId") {
            authHeader(userToken)
        }

    }

}