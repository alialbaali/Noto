package com.alialbaali.noto.di

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
import com.noto.local.LabelDao
import com.noto.local.LibraryDao
import com.noto.local.NotoDao
import com.noto.local.NotoDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://192.168.1.24:8080/"

private const val TIMEOUT = 60L

private val TIMEUNIT = TimeUnit.SECONDS

val repositoryModule = module {

    single { LabelRepositoryImpl(get<LabelDao>(), get<LabelClient>()) }

    single { NotoRepositoryImpl(get<NotoDao>(), get<NotoClient>()) }

    single { UserRepositoryImpl() }

    single { LibraryRepositoryImpl(get<LibraryDao>(), get<LibraryClient>()) }

}
val remoteDataSourceModule = module {

    single { HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY } }

    single { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    single {
        OkHttpClient.Builder().addInterceptor(get() as HttpLoggingInterceptor).callTimeout(TIMEOUT, TIMEUNIT).readTimeout(TIMEOUT, TIMEUNIT)
            .writeTimeout(TIMEOUT, TIMEUNIT).connectTimeout(TIMEOUT, TIMEUNIT).build()
    }

    single { Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(get())).baseUrl(BASE_URL).client(get()).build() }

    single { (get() as Retrofit).create(LabelClient::class.java) }

    single { (get() as Retrofit).create(LibraryClient::class.java) }

    single { (get() as Retrofit).create(UserClient::class.java) }

    single { (get() as Retrofit).create(NotoClient::class.java) }

}

val localDataSourceModule = module {

    single { NotoDatabase.getInstance(androidContext()).labelDao }

    single { NotoDatabase.getInstance(androidContext()).libraryDao }

    single { NotoDatabase.getInstance(androidContext()).notoDao }
}

val labelUseCasesModule = module {

    single { CreateLabel(get<LabelRepositoryImpl>()) }

    single { DeleteLabel(get<LabelRepositoryImpl>()) }

    single { GetLabels(get<LabelRepositoryImpl>()) }

    single { GetLabel(get<LabelRepositoryImpl>()) }

    single { UpdateLabel(get<LabelRepositoryImpl>()) }

    single { LabelUseCases(get(), get(), get(), get(), get()) }

}

val notoUseCasesModule = module {

    single { NotoUseCases(get(), get(), get(), get(), get()) }

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