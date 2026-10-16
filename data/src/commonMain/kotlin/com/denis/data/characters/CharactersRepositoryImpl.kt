package com.denis.data.characters

import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharactersEntity
import com.denis.domain.PreferencesHelper
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.get
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel
import kotlinx.serialization.json.Json

class CharactersRepositoryImpl(
    private val api: CharactersApi,
    private val mapper: CharactersMapper,
    private val preferencesHelper: PreferencesHelper
) : CharactersRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> {
        val cachedCharacters = if (name == null) getCachedCharacters(page) else null
        return if (cachedCharacters?.results.isNullOrEmpty()) {
            api.getCharacters(page, name).map { entity ->
                if (name == null) saveCharacters(entity, page)
                entity.results.forEach { saveCharacter(it) }
                mapper.toModel(entity)
            }
        } else {
            Result.success(mapper.toModel(cachedCharacters))
        }
    }

    override suspend fun getCharacter(id: Int): Result<CharacterModel> {
        val cached = getCachedCharacter(id)
        if (cached != null) return Result.success(mapper.toModel(cached))

        return api.getCharacter(id).map { entity ->
            saveCharacter(entity)
            mapper.toModel(entity)
        }
    }

    override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> {
        val cachedCharacters = ids.mapNotNull { getCachedCharacter(it) }
        if (cachedCharacters.size == ids.size) {
            return Result.success(cachedCharacters.map { mapper.toModel(it) })
        }

        return api.getCharacters(ids).map { list ->
            list.forEach { saveCharacter(it) }
            list.map { mapper.toModel(it) }
        }
    }

    private suspend fun saveCharacter(character: CharacterEntity) {
        preferencesHelper.set(characterKey(character.id), character)
    }

    private suspend fun saveCharacters(entity: CharactersEntity, page: Int) {
        preferencesHelper.set(charactersKey(page), entity)
    }

    private suspend fun getCachedCharacter(id: Int): CharacterEntity? {
        return preferencesHelper.get<CharacterEntity>(characterKey(id)).getOrNull()
    }

    private suspend fun getCachedCharacters(page: Int): CharactersEntity? {
        return preferencesHelper.get<CharactersEntity>(charactersKey(page)).getOrNull()
    }

    private fun charactersKey(page: Int) = "characters_$page"
    private fun characterKey(id: Int) = "character_$id"
}
