package com.denis.domain.locations

import com.denis.domain.FakeCharactersRepository
import com.denis.domain.FakeLocationsRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.Gender
import com.denis.domain.model.characters.Status
import com.denis.domain.model.locations.LocationModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadLocationDetailUseCaseTest {

    private lateinit var locationsRepository: FakeLocationsRepository
    private lateinit var charactersRepository: FakeCharactersRepository
    private lateinit var useCase: LoadLocationDetailUseCase

    private val location = LocationModel(
        id = 1,
        name = "Earth",
        type = "Planet",
        dimension = "C-137",
        residents = listOf("https://rickandmortyapi.com/api/character/1"),
        url = "url",
        created = "date"
    )

    private val character = CharacterModel(
        id = 1,
        name = "Rick Sanchez",
        status = Status.Alive,
        species = "Human",
        type = "",
        gender = Gender.Male,
        image = "image",
        episodeIds = emptyList()
    )

    @BeforeTest
    fun setUp() {
        locationsRepository = FakeLocationsRepository()
        charactersRepository = FakeCharactersRepository()
        useCase = LoadLocationDetailUseCase(locationsRepository, charactersRepository)
    }

    @Test
    fun `full flow success`() = runTest {
        locationsRepository.getLocationResult = Result.success(location)
        charactersRepository.getCharactersListResult = Result.success(listOf(character))

        var locationResult: Result<LocationModel>? = null
        var idsResult: Result<List<Int>>? = null
        var charactersResult: Result<List<CharacterModel>>? = null

        useCase.getLocation(1) { locationResult = this }
            .checkResidentsUrls { idsResult = this }
            .getResidents { charactersResult = this }

        assertTrue(locationResult!!.isSuccess)
        assertEquals(location, locationResult!!.getOrNull())
        assertEquals(listOf(1), idsResult!!.getOrNull())
        assertEquals(listOf(character), charactersResult!!.getOrNull())
    }

    @Test
    fun `getLocation failure stops execution`() = runTest {
        locationsRepository.getLocationResult = Result.failure(Exception("Network error"))

        var locationResult: Result<LocationModel>? = null
        var idsResult: Result<List<Int>>? = null

        useCase.getLocation(1) { locationResult = this }
            .checkResidentsUrls { idsResult = this }

        assertTrue(locationResult!!.isFailure)
        assertEquals(null, idsResult)
    }

    @Test
    fun `checkResidentsUrls failure stops execution`() = runTest {
        locationsRepository.getLocationResult = Result.success(location.copy(residents = emptyList()))

        var idsResult: Result<List<Int>>? = null
        var charactersResult: Result<List<CharacterModel>>? = null

        useCase.getLocation(1)
            .checkResidentsUrls { idsResult = this }
            .getResidents { charactersResult = this }

        assertTrue(idsResult!!.isFailure)
        assertEquals(null, charactersResult)
    }
}
