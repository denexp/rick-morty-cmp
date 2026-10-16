package com.denis.rickmorty.episodes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.episodes.EpisodeModel

data class EpisodesListUiData(
    val episodesInfo: InfoModel = InfoModel(),
    val episodesMap: Map<Int, List<EpisodeModel>> = emptyMap(),
    val expandedSeasons: Set<String> = emptySet(),
    val searchQuery: String = "",
    val state: UiState = UiState.Loading,
    val scrollState: MutableState<Int> = mutableIntStateOf(0),
) {
    fun episodes() = episodesMap.values.flatten()

    fun episodesBySeason(): Map<String, List<EpisodeModel>> {
        return episodes().groupBy { it.episode.substringBefore("E") }
    }
}
