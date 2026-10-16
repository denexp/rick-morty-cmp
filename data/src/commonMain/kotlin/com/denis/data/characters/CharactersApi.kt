package com.denis.data.characters

import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharactersEntity
import com.denis.data.network.NetworkApi
import com.denis.data.network.get

open class CharactersApi(private val api: NetworkApi) {
    open suspend fun getCharacters(page: Int, name: String?) = api.get<CharactersEntity>("api/character", query = mapOf("page" to page, "name" to name))
    open suspend fun getCharacter(id: Int) = api.get<CharacterEntity>("api/character/$id")
    open suspend fun getCharacters(ids: List<Int>): Result<List<CharacterEntity>> {
        if (ids.isEmpty()) return Result.success(emptyList())
        return if (ids.size == 1) {
            api.get<CharacterEntity>("api/character/${ids[0]}").map { listOf(it) }
        } else {
            api.get<List<CharacterEntity>>("api/character/${ids.joinToString(",")}")
        }
    }
}