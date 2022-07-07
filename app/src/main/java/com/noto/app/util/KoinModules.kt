package com.noto.app.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.noto.app.AppViewModel
import com.noto.app.BuildConfig
import com.noto.app.allnotes.AllNotesViewModel
import com.noto.app.data.database.NotoDatabase
import com.noto.app.data.repository.*
import com.noto.app.data.source.RemoteAuthClient
import com.noto.app.data.source.RemoteUserClient
import com.noto.app.domain.repository.*
import com.noto.app.domain.source.*
import com.noto.app.folder.FolderViewModel
import com.noto.app.label.LabelViewModel
import com.noto.app.main.MainViewModel
import com.noto.app.note.NoteViewModel
import com.noto.app.recentnotes.RecentNotesViewModel
import com.noto.app.settings.SettingsViewModel
import com.noto.app.widget.FolderListWidgetConfigViewModel
import com.noto.app.widget.NoteListWidgetConfigViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

private const val DataStoreName = "Noto Data Store"
private val Context.dataStore by preferencesDataStore(name = DataStoreName)
private val AuthClientQualifier = qualifier("AuthClient")
private val ClientQualifier = qualifier("Client")

val appModule = module {

    viewModel { MainViewModel(get(), get(), get()) }

    viewModel { FolderViewModel(get(), get(), get(), get(), get(), it.get()) }

    viewModel {
        NoteViewModel(get(),
            get(),
            get(),
            get(),
            get(),
            it[0],
            it[1],
            it.getOrNull(),
            it.getOrNull() ?: longArrayOf())
    }

    viewModel { AppViewModel(get(), get()) }

    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }

    viewModel { LabelViewModel(get(), get(), it[0], it[1]) }

    viewModel { FolderListWidgetConfigViewModel(it.get(), get(), get(), get()) }

    viewModel { NoteListWidgetConfigViewModel(it.get(), get(), get(), get(), get(), get()) }

    viewModel { AllNotesViewModel(get(), get(), get(), get(), get()) }

    viewModel { RecentNotesViewModel(get(), get(), get(), get(), get()) }
}

val repositoryModule = module {

    single<FolderRepository> { FolderRepositoryImpl(get()) }

    single<NoteRepository> { NoteRepositoryImpl(get()) }

    single<LabelRepository> { LabelRepositoryImpl(get()) }

    single<NoteLabelRepository> { NoteLabelRepositoryImpl(get()) }

    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

}

val localDataSourceModule = module {

    single<LocalFolderDataSource> { NotoDatabase.getInstance(androidContext()).folderDao }

    single<LocalNoteDataSource> { NotoDatabase.getInstance(androidContext()).noteDao }

    single<LocalLabelDataSource> { NotoDatabase.getInstance(androidContext()).labelDao }

    single<LocalNoteLabelDataSource> { NotoDatabase.getInstance(androidContext()).noteLabelDao }

    single<DataStore<Preferences>> { androidContext().dataStore }

}

val remoteDataSourceModule = module {

    single(AuthClientQualifier) { DefaultHttpClient() }

    single(ClientQualifier) {
        val settingsRepository by inject<SettingsRepository>()
        val authDataSource by inject<RemoteAuthDataSource>(AuthClientQualifier)
        DefaultHttpClient {
            Auth {
                bearer {
                    sendWithoutRequest { true }
                    loadTokens {
                        val accessToken = settingsRepository.accessToken.firstOrNull()
                        val refreshToken = settingsRepository.refreshToken.firstOrNull()
                        if (accessToken != null && refreshToken != null)
                            BearerTokens(accessToken, refreshToken)
                        else
                            null
                    }
                    refreshTokens {
                        val refreshToken = settingsRepository.refreshToken.firstOrNull()
                        if (refreshToken != null) {
                            val response = authDataSource.refreshToken(refreshToken)
                            settingsRepository.updateAccessToken(response.accessToken)
                            settingsRepository.updateRefreshToken(response.refreshToken)
                            BearerTokens(response.accessToken, response.refreshToken)
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }

    single<RemoteAuthDataSource> { RemoteAuthClient(get(ClientQualifier)) }

    single<RemoteUserDataSource> { RemoteUserClient(get(ClientQualifier)) }
}

private fun DefaultHttpClient(block: HttpClientConfig<OkHttpConfig>.() -> Unit = {}) = HttpClient(OkHttp) {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "bcehffsgkofhyjoktqpe.supabase.co"
        }
        header(Constants.ApiKey, BuildConfig.SupabaseApiKey)
        contentType(ContentType.Application.Json)
    }
    install(ContentNegotiation) {
        json(NotoDefaultJson)
    }
    if (BuildConfig.DEBUG) {
        Logging {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
        }
    }
    block()
}