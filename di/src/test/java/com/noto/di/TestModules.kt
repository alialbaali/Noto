package com.noto.di

import com.fasterxml.jackson.databind.SerializationFeature
import com.noto.data.repository.UserRepositoryTest
import com.noto.local.FakeUserDao
import com.noto.remote.UserClientTest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.Json
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.dsl.module

val repositoryTestModule = module {

    single { UserRepositoryTest(get<FakeUserDao>(), get<UserClientTest>()) }

}

val remoteTestModule = module {

    single {
        HttpClient(MockEngine) {

            expectSuccess = false

            defaultRequest {
                contentType(ContentType.Application.Json)
            }

            Json {
                serializer = JacksonSerializer {
                    enable(SerializationFeature.INDENT_OUTPUT)
                    writerWithDefaultPrettyPrinter()
                }
            }

            Logging {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }


}

val localTestModule = module {
    single { FakeUserDao() }
}
