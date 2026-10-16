package com.denis.domain.characters

import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel

interface CharactersRepository {
    suspend fun getCharacters(page: Int = 1, name: String? = null): Result<CharactersModel>
    suspend fun getCharacter(id: Int): Result<CharacterModel>
    suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>>
}