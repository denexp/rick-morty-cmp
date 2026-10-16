package com.denis.rickmorty.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.domain.characters.LoadCharacterDetailUseCase
import com.denis.domain.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val loadCharacterDetailUseCase: LoadCharacterDetailUseCase
) : ViewModel() {
    private val _data: MutableStateFlow<CharactersDetailUiData> = MutableStateFlow(
        CharactersDetailUiData()
    )
    val data = _data.asStateFlow()

    fun character(id: Int) {
        viewModelScope.launch {
            loadCharacterDetailUseCase
                .getCharacter(id) {
                    onSuccess { result ->
                        _data.update { it.copy(character = result) }
                    }
                    onFailure { fail ->
                        _data.update { it.copy(state = UiState.Error(fail)) }
                    }
                }
                .checkEpisodeUrls {
                    onFailure {
                        _data.update { it.copy(state = UiState.Content) }
                    }
                }
                .getEpisodes {
                    onSuccess { episodes ->
                        _data.update { it.copy(episodes = episodes, state = UiState.Content) }
                    }
                    onFailure { fail ->
                        _data.update { it.copy(state = UiState.Error(fail)) }
                    }
                }
        }
    }
}
