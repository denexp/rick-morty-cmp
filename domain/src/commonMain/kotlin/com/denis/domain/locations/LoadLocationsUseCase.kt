package com.denis.domain.locations

import com.denis.domain.BaseUseCase
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.locations.LocationsModel

class LoadLocationsUseCase(
    private val locationsRepository: LocationsRepository,
    private val charactersRepository: CharactersRepository
) : BaseUseCase() {

    suspend fun getLocations(
        page: Int = 1,
        name: String? = null,
        send: Result<LocationsModel>.() -> Unit = {}
    ): LoadLocationsUseCase {
        clear()
        return execute {
            val result = locationsRepository.getLocations(page, name)
            save(result)
            send(result)
        }
    }

    suspend fun getResidents(
        residentIds: List<Int>,
        send: Result<List<CharacterModel>>.() -> Unit = {}
    ): LoadLocationsUseCase {
        return execute {
            val result = charactersRepository.getCharacters(residentIds)
            save(result)
            send(result)
        }
    }
}
