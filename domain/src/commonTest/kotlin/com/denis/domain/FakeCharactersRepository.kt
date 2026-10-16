package com.denis.domain

import com.denis.domain.characters.CharactersRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel

class FakeCharactersRepository : CharactersRepository {
    var getCharactersResult: Result<CharactersModel> = Result.failure(Exception("Not implemented"))
    var getCharacterResult: Result<CharacterModel> = Result.failure(Exception("Not implemented"))
    var getCharactersListResult: Result<List<CharacterModel>> = Result.failure(Exception("Not implemented"))

    override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> = getCharactersResult
    override suspend fun getCharacter(id: Int): Result<CharacterModel> = getCharacterResult
    override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> = getCharactersListResult
}
