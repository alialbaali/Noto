package com.noto.di

import android.content.Context
import com.noto.data.repository.LibraryRepositoryImpl
import com.noto.data.repository.NotoRepositoryImpl
import com.noto.data.repository.SyncRepositoryImpl
import com.noto.data.repository.UserRepositoryImpl
import com.fasterxml.jackson.databind.SerializationFeature
import com.noto.domain.interactor.FetchData
import com.noto.domain.interactor.SyncData
import com.noto.domain.interactor.library.*
import com.noto.domain.interactor.noto.*
import com.noto.domain.interactor.user.CreateUser
import com.noto.domain.interactor.user.GetUserToken
import com.noto.domain.interactor.user.LoginUser
import com.noto.domain.interactor.user.UserUseCases
import com.noto.local.*
import com.noto.remote.LibraryClient
import com.noto.remote.NotoClient
import com.noto.remote.UserClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.Json
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

private const val HOST = "api-noto.herokuapp.com"

private const val PORT = 8080

private const val TIMEOUT = 60L

private val TIMEUNIT = TimeUnit.SECONDS

private const val SHARED_PREFERENCES_NAME = "Noto Shared Preferences"

val repositoryModule = module {

    single { NotoRepositoryImpl(get<UserDao>(), get<EntityStatusDao>(), get<NotoDao>(), get<NotoClient>()) }

    single { UserRepositoryImpl(get<UserDao>(), get<UserClient>()) }

    single { LibraryRepositoryImpl(get<UserDao>(), get<EntityStatusDao>(), get<LibraryDao>(), get<LibraryClient>()) }

    single { SyncRepositoryImpl(get<UserDao>(), get<EntityStatusDao>(), get<LibraryDao>(), get<LibraryClient>(), get<NotoDao>(), get<NotoClient>()) }

    single<SyncData> { SyncData(get<SyncRepositoryImpl>()) }

    single<FetchData> { FetchData(get<SyncRepositoryImpl>()) }

}
val remoteDataSourceModule = module {

    single {
        HttpClient(OkHttp) {

            expectSuccess = false

            defaultRequest {
                host = HOST
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

    single { UserClient(get()) }

    single { LibraryClient(get()) }

    single { NotoClient(get()) }

}

val localDataSourceModule = module {

    single<LibraryDao> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<NotoDao> { NotoDatabase.getInstance(androidContext()).notoDao }

    single<UserDao> { UserDao(androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)) }

    single<EntityStatusDao> { NotoDatabase.getInstance(androidContext()).entityStatusDao }
}

val notoUseCasesModule = module {

    single {
        NotoUseCases(
            get<CreateNoto>(),
            get<UpdateNoto>(),
            get<DeleteNoto>(),
            get<GetNoto>(),
            get<GetNotos>(),
            get<CountLibraryNotos>()
        )
    }

    single { CreateNoto(get<NotoRepositoryImpl>()) }

    single { UpdateNoto(get<NotoRepositoryImpl>()) }

    single { DeleteNoto(get<NotoRepositoryImpl>()) }

    single { GetNotos(get<NotoRepositoryImpl>()) }

    single { GetNoto(get<NotoRepositoryImpl>()) }

    single { CountLibraryNotos(get<NotoRepositoryImpl>()) }

}

val libraryUseCasesModule = module {

    single { LibraryUseCases(get(), get(), get(), get(), get(), get()) }

    single { CreateLibrary(get<LibraryRepositoryImpl>()) }

    single { UpdateLibrary(get<LibraryRepositoryImpl>()) }

    single { DeleteLibrary(get<LibraryRepositoryImpl>()) }

    single { GetLibraryById(get<LibraryRepositoryImpl>()) }

    single { GetLibraries(get<LibraryRepositoryImpl>()) }

    single { CountNotos(get<LibraryRepositoryImpl>()) }

}

val userUseCasesModule = module {

    single { UserUseCases(get(), get(), get(), get()) }

    single { CreateUser(get<UserRepositoryImpl>()) }

    single { LoginUser(get<UserRepositoryImpl>()) }

    single { GetUserToken(get<UserRepositoryImpl>()) }

}