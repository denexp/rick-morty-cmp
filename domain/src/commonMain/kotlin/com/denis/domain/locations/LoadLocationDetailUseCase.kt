package com.denis.domain.locations

import com.denis.domain.BaseUseCase
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.locations.LocationModel

class LoadLocationDetailUseCase(
    private val locationsRepository: LocationsRepository,
    private val charactersRepository: CharactersRepository
) : BaseUseCase() {

    suspend fun getLocation(id: Int, send: Result<LocationModel>.() -> Unit = {}): LoadLocationDetailUseCase {
        clear()
        return execute<LoadLocationDetailUseCase> {
            val result = locationsRepository.getLocation(id)
            save(result)
            send(result)
        }.stopOnError()
    }

    suspend fun checkResidentsUrls(send: Result<List<Int>>.() -> Unit = {}): LoadLocationDetailUseCase {
        return execute {
            val ids = runCatching {
                val residentUrls: List<String> = lastResult<LocationModel>().getOrThrow().residents
                val ids = residentUrls.mapNotNull { it.split("/").lastOrNull()?.toIntOrNull() }
                if (ids.isEmpty())
                    throw Exception("No residents found")
                ids
            }
            save(ids)
            send(ids)
        }
    }

    suspend fun getResidents(send: Result<List<CharacterModel>>.() -> Unit = {}): LoadLocationDetailUseCase {
        val ids = lastResult<List<Int>>().getOrNull() ?: return this
        return execute {
            send(charactersRepository.getCharacters(ids))
        }
    }
}
