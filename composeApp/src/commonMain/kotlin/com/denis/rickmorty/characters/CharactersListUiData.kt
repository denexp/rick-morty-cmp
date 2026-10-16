package com.denis.rickmorty.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.InfoModel

data class CharactersListUiData(
    val charactersInfo: InfoModel = InfoModel(),
    val charactersMap: Map<Int, List<CharacterModel>> = emptyMap(),
    val searchQuery: String = "",
    val page: Int = 1,
    val state: UiState = UiState.Loading,
    val scrollState: MutableState<Int> = mutableIntStateOf(0),
) {
    fun characters() = charactersMap.values.flatten()
}