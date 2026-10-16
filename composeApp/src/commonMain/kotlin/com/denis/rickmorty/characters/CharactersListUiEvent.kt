package com.denis.rickmorty.characters

sealed class CharactersListUiEvent {
    data class CharacterDetail(val id: Int): CharactersListUiEvent()
    data class SearchCharacters(val nextPage: Boolean = false, val name: String? = null): CharactersListUiEvent()
    data class Reload(val name: String? = null): CharactersListUiEvent()
}