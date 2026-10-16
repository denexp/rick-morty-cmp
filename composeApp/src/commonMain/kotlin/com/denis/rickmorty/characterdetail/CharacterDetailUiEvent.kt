package com.denis.rickmorty.characterdetail

sealed class CharacterDetailUiEvent {
    data object Reload: CharacterDetailUiEvent()
    data class OpenEpisode(val id: Int): CharacterDetailUiEvent()
}
