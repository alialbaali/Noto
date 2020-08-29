package com.noto.remote

import com.noto.domain.schema.ResponseSchema
import com.noto.domain.schema.UserResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.hostWithPort
import io.ktor.util.AttributeKey

class UserClientTest {

    private val httpClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                when (request.url.fullUrl) {
                    "http://0.0.0.0:8080/user/create" -> {
//                        request.body.getProperty(AttributeKey(""))
//                        val userResponse = UserResponse()
//                        val response = ResponseSchema<UserResponse>(true, null, userResponse)

//                        respond
                        TODO()
                    }


                }

            }
        }
    }

}

private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPort$fullPath"