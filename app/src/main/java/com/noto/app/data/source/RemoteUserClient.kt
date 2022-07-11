package com.noto.app.data.source

import com.noto.app.data.model.remote.RemoteUser
import com.noto.app.data.model.remote.RestErrorResponse
import com.noto.app.domain.source.RemoteUserDataSource
import com.noto.app.util.Constants
import com.noto.app.util.getOrElse
import com.noto.app.util.unhandledError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class RemoteUserClient(private val client: HttpClient) : RemoteUserDataSource {

    override suspend fun getUser(): RemoteUser {
        return client.get("/rest/v1/users") {
            parameter(Constants.Select, "*")
        }.getOrElse<List<RemoteUser>> { response ->
            val errorResponse = response.body<RestErrorResponse>()
            unhandledError(errorResponse.message)
        }.first()
    }

    override suspend fun createUser(id: String, name: String) {
        return client.post("/rest/v1/users") {
            setBody(
                mapOf(
                    Constants.Id to id,
                    Constants.Name to name,
                )
            )
        }.getOrElse { response ->
            val errorResponse = response.body<RestErrorResponse>()
            unhandledError(errorResponse.message)
        }
    }

    override suspend fun updateName(id: String, name: String) {
        return client.patch("/rest/v1/users") {
            parameter(Constants.Id, FilterKeys eq id)
            setBody(
                mapOf(Constants.Name to name)
            )
        }.getOrElse { response ->
            val errorResponse = response.body<RestErrorResponse>()
            unhandledError(errorResponse.message)
        }
    }
}