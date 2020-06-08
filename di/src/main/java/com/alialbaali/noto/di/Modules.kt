package com.alialbaali.noto.di

import android.content.Context
import com.alialbaali.noto.data.repository.LabelRepositoryImpl
import com.alialbaali.noto.data.repository.LibraryRepositoryImpl
import com.alialbaali.noto.data.repository.NotoRepositoryImpl
import com.alialbaali.noto.data.repository.UserRepositoryImpl
import com.alialbaali.noto.remote.LabelClient
import com.alialbaali.noto.remote.LibraryClient
import com.alialbaali.noto.remote.NotoClient
import com.alialbaali.noto.remote.UserClient
import com.noto.domain.interactor.label.*
import com.noto.domain.interactor.library.*
import com.noto.domain.interactor.noto.*
import com.noto.domain.interactor.user.CreateUser
import com.noto.domain.interactor.user.LoginUser
import com.noto.domain.interactor.user.UserUseCases
import com.noto.local.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.1.24:8080"

private const val TIMEOUT = 60L

private val TIMEUNIT = TimeUnit.SECONDS

private const val SHARED_PREFERENCES_NAME = "Noto Shared Preferences"

val repositoryModule = module {

    single { LabelRepositoryImpl(get<LabelDao>(), get<LabelClient>()) }

    single { NotoRepositoryImpl(get<NotoDao>(), get<NotoClient>()) }

    single { UserRepositoryImpl(get<UserDao>(), get<UserClient>()) }

    single { LibraryRepositoryImpl(get<LibraryDao>(), get<LibraryClient>()) }

}
val remoteDataSourceModule = module {

    single<HttpLoggingInterceptor> { HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY } }

    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .callTimeout(TIMEOUT, TIMEUNIT)
            .readTimeout(TIMEOUT, TIMEUNIT)
            .writeTimeout(TIMEOUT, TIMEUNIT)
            .connectTimeout(TIMEOUT, TIMEUNIT)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .baseUrl(BASE_URL)
            .client(get<OkHttpClient>())
            .build()
    }

//    factory<Converter<ResponseBody, ResponseSchema<Any>>> {
//        println("CREATED")
//        get<Retrofit>().responseBodyConverter(
//            ResponseSchema::class.java,
//            arrayOfNulls<Annotation>(0)
//        )
//    }

//    factory { ErrorConverter.invoke(get()) }

    single<LabelClient> { get<Retrofit>().create(LabelClient::class.java) }

    single<LibraryClient> { get<Retrofit>().create(LibraryClient::class.java) }

    single<UserClient> { get<Retrofit>().create(UserClient::class.java) }

    single<NotoClient> { get<Retrofit>().create(NotoClient::class.java) }

}

val localDataSourceModule = module {

    single<LabelDao> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<LibraryDao> { NotoDatabase.getInstance(androidContext()).libraryDao }

    single<NotoDao> { NotoDatabase.getInstance(androidContext()).notoDao }

    single<UserDao> { UserDao(androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)) }
}

val labelUseCasesModule = module {

    single {
        LabelUseCases(
            get<CreateLabel>(),
            get<DeleteLabel>(),
            get<UpdateLabel>(),
            get<GetLabel>(),
            get<GetLabels>()
        )
    }

    single { CreateLabel(get<LabelRepositoryImpl>()) }

    single { DeleteLabel(get<LabelRepositoryImpl>()) }

    single { GetLabels(get<LabelRepositoryImpl>()) }

    single { GetLabel(get<LabelRepositoryImpl>()) }

    single { UpdateLabel(get<LabelRepositoryImpl>()) }


}

val notoUseCasesModule = module {

    single {
        NotoUseCases(
            get<CreateNoto>(),
            get<UpdateNoto>(),
            get<DeleteNoto>(),
            get<GetNoto>(),
            get<GetNotos>()
        )
    }

    single { CreateNoto(get<NotoRepositoryImpl>()) }

    single { UpdateNoto(get<NotoRepositoryImpl>()) }

    single { DeleteNoto(get<NotoRepositoryImpl>()) }

    single { GetNotos(get<NotoRepositoryImpl>()) }

    single { GetNoto(get<NotoRepositoryImpl>()) }

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

    single { UserUseCases(get(), get()) }

    single { CreateUser(get<UserRepositoryImpl>()) }

    single { LoginUser(get<UserRepositoryImpl>()) }

}