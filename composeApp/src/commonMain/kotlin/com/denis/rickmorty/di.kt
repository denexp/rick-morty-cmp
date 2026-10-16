package com.denis.rickmorty

import com.denis.di.layerModules
import com.denis.rickmorty.characters.CharactersListViewModel
import com.denis.rickmorty.characterdetail.CharacterDetailViewModel
import com.denis.rickmorty.episodedetail.EpisodeDetailViewModel
import com.denis.rickmorty.episodes.EpisodesListViewModel
import com.denis.rickmorty.locations.LocationsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CharactersListViewModel(get()) }
    viewModel { CharacterDetailViewModel(get()) }
    viewModel { EpisodesListViewModel(get()) }
    viewModel { EpisodeDetailViewModel(get()) }
    viewModel { LocationsListViewModel(get()) }
}

fun koinModules() = appModule + layerModules()