package com.denis.rickmorty.characterdetail

import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel

data class CharactersDetailUiData(
    val character: CharacterModel = CharacterModel(),
    val episodes: List<EpisodeModel> = emptyList(),
    val state: UiState = UiState.Loading,
)
