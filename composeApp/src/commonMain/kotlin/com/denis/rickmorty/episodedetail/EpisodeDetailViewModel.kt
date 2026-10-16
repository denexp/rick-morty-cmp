package com.denis.rickmorty.episodedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.domain.episodes.LoadEpisodeDetailUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EpisodeDetailUiData(
    val episode: EpisodeModel = EpisodeModel(),
    val characters: List<CharacterModel> = emptyList(),
    val state: UiState = UiState.Loading
)

class EpisodeDetailViewModel(
    private val loadEpisodeDetailUseCase: LoadEpisodeDetailUseCase,
) : ViewModel() {
    private val _data = MutableStateFlow(EpisodeDetailUiData())
    val data = _data.asStateFlow()

    fun loadEpisode(id: Int) {
        viewModelScope.launch {
            loadEpisodeDetailUseCase
                .getEpisode(id) {
                    onFailure { fail ->
                        _data.update { it.copy(state = UiState.Error(fail)) }
                    }
                    onSuccess { value ->
                        _data.update { it.copy(episode = value) }
                    }
                }
                .checkCharactersUrls {
                    onFailure { fail ->
                        _data.update { it.copy(state = UiState.Content) }
                    }
                }
                .getCharacters {
                    onSuccess { value ->
                        _data.update { it.copy(characters = value, state = UiState.Content) }
                    }
                }
        }
    }
}
