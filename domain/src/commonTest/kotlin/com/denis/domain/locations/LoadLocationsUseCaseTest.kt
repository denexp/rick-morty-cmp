package com.denis.domain.locations

import com.denis.domain.FakeCharactersRepository
import com.denis.domain.FakeLocationsRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.locations.LocationsModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadLocationsUseCaseTest {

    private lateinit var locationsRepository: FakeLocationsRepository
    private lateinit var charactersRepository: FakeCharactersRepository
    private lateinit var useCase: LoadLocationsUseCase

    @BeforeTest
    fun setUp() {
        locationsRepository = FakeLocationsRepository()
        charactersRepository = FakeCharactersRepository()
        useCase = LoadLocationsUseCase(locationsRepository, charactersRepository)
    }

    @Test
    fun `getLocations success`() = runTest {
        val expected = LocationsModel(info = InfoModel(count = 1), results = emptyList())
        locationsRepository.getLocationsResult = Result.success(expected)

        var actual: Result<LocationsModel>? = null
        useCase.getLocations(1) { actual = this }

        assertTrue(actual!!.isSuccess)
        assertEquals(expected, actual!!.getOrNull())
    }

    @Test
    fun `getResidents success`() = runTest {
        val expected = listOf(CharacterModel(id = 1, name = "Rick"))
        charactersRepository.getCharactersListResult = Result.success(expected)

        var actual: Result<List<CharacterModel>>? = null
        useCase.getResidents(listOf(1)) { actual = this }

        assertTrue(actual!!.isSuccess)
        assertEquals(expected, actual!!.getOrNull())
    }
}
