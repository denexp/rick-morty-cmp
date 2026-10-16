package com.denis.rickmorty.episodes

sealed class EpisodesListUiEvent {
    data class SearchEpisodes(val name: String? = null) : EpisodesListUiEvent()
    data class ToggleSeason(val season: String): EpisodesListUiEvent()
    data class EpisodeDetail(val id: Int): EpisodesListUiEvent()
}
