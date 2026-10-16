package com.denis.data.di

import com.denis.data.AppPreferencesHelper
import com.denis.data.characters.CharactersApi
import com.denis.data.characters.CharactersMapper
import com.denis.data.characters.CharactersRepositoryImpl
import com.denis.data.episodes.EpisodesApi
import com.denis.data.episodes.EpisodesMapper
import com.denis.data.episodes.EpisodesRepositoryImpl
import com.denis.data.locations.LocationsApi
import com.denis.data.locations.LocationsMapper
import com.denis.data.locations.LocationsRepositoryImpl
import com.denis.data.network.NetworkApi
import com.denis.data.network.NetworkApiImpl
import com.denis.domain.Log
import com.denis.domain.PreferencesHelper
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.locations.LocationsRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<PreferencesHelper> { AppPreferencesHelper(get()) }

    single(named(HttpClientType.RickAndMorty)) {
        HttpClient {
            install(Logging) {
                level = LogLevel.ALL
                logger = object: Logger {
                    override fun log(message: String) {
                        Log.e("HttpClient", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                host = HttpClientType.RickAndMorty.host
                url {
                    protocol = URLProtocol.HTTPS
                }
            }
        }
    }

    single<NetworkApi> { NetworkApiImpl(get(named(HttpClientType.RickAndMorty))) }

    single<CharactersRepository> {
        CharactersRepositoryImpl(
            CharactersApi(get()),
            CharactersMapper(),
            get()
        )
    }

    single { CharactersMapper() }

    single { EpisodesMapper() }
    single { EpisodesApi(get()) }
    single<EpisodesRepository> {
        EpisodesRepositoryImpl(get(), get(), get())
    }

    single { LocationsMapper() }
    single { LocationsApi(get()) }
    single<LocationsRepository> {
        LocationsRepositoryImpl(get(), get(), get())
    }
}
private enum class HttpClientType(val host: String) {
    RickAndMorty("rickandmortyapi.com")
}