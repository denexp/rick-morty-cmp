package com.denis.domain.di

import com.denis.domain.characters.LoadCharacterDetailUseCase
import com.denis.domain.characters.LoadCharactersUseCase
import com.denis.domain.episodes.LoadEpisodeDetailUseCase
import com.denis.domain.episodes.LoadEpisodesUseCase
import com.denis.domain.locations.LoadLocationDetailUseCase
import com.denis.domain.locations.LoadLocationsUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoadCharactersUseCase(get()) }
    factory { LoadCharacterDetailUseCase(get(), get()) }
    factory { LoadEpisodesUseCase(get()) }
    factory { LoadEpisodeDetailUseCase(get(), get()) }
    factory { LoadLocationDetailUseCase(get(), get()) }
    factory { LoadLocationsUseCase(get(), get()) }
}
