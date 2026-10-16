package com.denis.rickmorty.episodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.domain.episodes.LoadEpisodesUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.episodes.EpisodeModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EpisodesListViewModel(private val loadEpisodesUseCase: LoadEpisodesUseCase) : ViewModel() {
    private val _data = MutableStateFlow(EpisodesListUiData())
    val data = _data.asStateFlow()
    private var searchJob: Job? = null

    fun loadEpisodes(name: String? = null) {
        if (name == null && _data.value.episodesMap.isNotEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            // If name is null, we use the already stored searchQuery (e.g. on screen resume)
            val query = name ?: _data.value.searchQuery

            
            _data.update { 
                it.copy(
                    episodesMap = it.episodesMap,
                    searchQuery = query
                ) 
            }

            val allEpisodes = mutableListOf<EpisodeModel>()
            var currentPage = 1
            var totalPages = 1

            do {
                loadEpisodesUseCase.getEpisodes(currentPage, query.ifEmpty { null }) {
                    fold(
                        onSuccess = { result ->
                            allEpisodes.addAll(result.results)
                            totalPages = result.info.pages
                            currentPage++
                        },
                        onFailure = { fail ->
                            if (allEpisodes.isEmpty()) {
                                _data.update { it.copy(state = UiState.Error(fail)) }
                            }
                            totalPages = 0
                        }
                    )
                }
            } while (currentPage <= totalPages)

            if (allEpisodes.isNotEmpty())
                _data.update {
                    it.copy(
                        episodesMap = mapOf(1 to allEpisodes),
                        state = UiState.Content,
                        searchQuery = query
                    )
                }
        }
    }

    fun toggleSeason(season: String) {
        _data.update {
            val expanded = it.expandedSeasons.toMutableSet()
            if (expanded.contains(season)) {
                expanded.remove(season)
            } else {
                expanded.add(season)
            }
            it.copy(expandedSeasons = expanded)
        }
    }
}
