package com.denis.rickmorty.locations

import com.denis.domain.locations.LoadLocationsUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.locations.LocationsRepository
import com.denis.domain.model.characters.CharactersModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LocationsListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeLocationsRepository: FakeLocationsRepository
    private lateinit var fakeCharactersRepository: FakeCharactersRepository
    private lateinit var loadLocationsUseCase: LoadLocationsUseCase
    private lateinit var viewModel: LocationsListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeLocationsRepository = FakeLocationsRepository()
        fakeCharactersRepository = FakeCharactersRepository()
        loadLocationsUseCase = LoadLocationsUseCase(fakeLocationsRepository, fakeCharactersRepository)
        viewModel = LocationsListViewModel(loadLocationsUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLocations success should update state to Content`() = runTest {
        val locations = listOf(LocationModel(id = 1, name = "Earth"))
        val locationsModel = LocationsModel(info = InfoModel(count = 1, pages = 1), results = locations)
        fakeLocationsRepository.getLocationsResult = Result.success(locationsModel)

        viewModel.loadLocations()
        advanceUntilIdle()

        val state = viewModel.data.value
        assertEquals(UiState.Content, state.state)
        assertEquals(locations, state.locations)
        assertEquals(1, state.page)
    }

    @Test
    fun `loadLocations failure should update state to Error`() = runTest {
        val exception = Exception("Network error")
        fakeLocationsRepository.getLocationsResult = Result.failure(exception)

        viewModel.loadLocations()
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.state is UiState.Error)
        assertEquals(exception, (state.state as UiState.Error).value)
    }

    @Test
    fun `toggleLocation should expand and fetch residents if not already fetched`() = runTest {
        val locationId = 1
        val residentUrls = listOf("https://rickandmortyapi.com/api/character/1")
        val locations = listOf(LocationModel(id = locationId, name = "Earth", residents = residentUrls))
        val locationsModel = LocationsModel(results = locations)
        fakeLocationsRepository.getLocationsResult = Result.success(locationsModel)
        
        val characters = listOf(CharacterModel(id = 1, name = "Rick Sanchez"))
        fakeCharactersRepository.getCharactersListResult = Result.success(characters)

        // Load locations first
        viewModel.loadLocations()
        advanceUntilIdle()

        // Toggle to expand
        viewModel.toggleLocation(locationId)
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.expandedLocations.contains(locationId))
        assertEquals(characters, state.residents[locationId])
    }

    private class FakeLocationsRepository : LocationsRepository {
        var getLocationsResult: Result<LocationsModel> = Result.failure(Exception())
        override suspend fun getLocations(page: Int, name: String?, type: String?, dimension: String?): Result<LocationsModel> = getLocationsResult
        override suspend fun getLocation(id: Int): Result<LocationModel> = Result.failure(Exception())
        override suspend fun getLocations(ids: List<Int>): Result<List<LocationModel>> = Result.failure(Exception())
    }

    private class FakeCharactersRepository : CharactersRepository {
        var getCharactersListResult: Result<List<CharacterModel>> = Result.failure(Exception())
        override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> = Result.failure(Exception())
        override suspend fun getCharacter(id: Int): Result<CharacterModel> = Result.failure(Exception())
        override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> = getCharactersListResult
    }
}
