package com.denis.rickmorty.characters

import com.denis.domain.characters.LoadCharactersUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.characters.CharactersRepository
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
class CharactersListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeCharactersRepository: FakeCharactersRepository
    private lateinit var loadCharactersUseCase: LoadCharactersUseCase
    private lateinit var viewModel: CharactersListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeCharactersRepository = FakeCharactersRepository()
        loadCharactersUseCase = LoadCharactersUseCase(fakeCharactersRepository)
        viewModel = CharactersListViewModel(loadCharactersUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchCharacters success should update state to Content`() = runTest {
        val characters = listOf(CharacterModel(id = 1, name = "Rick Sanchez"))
        val charactersModel = CharactersModel(info = InfoModel(count = 1, pages = 1), results = characters)
        fakeCharactersRepository.getCharactersResult = Result.success(charactersModel)

        viewModel.searchCharacters(page = 1, name = "Rick")
        advanceUntilIdle()

        val state = viewModel.data.value
        assertEquals(UiState.Content, state.state)
        assertEquals(characters, state.characters())
        assertEquals("Rick", state.searchQuery)
    }

    @Test
    fun `searchCharacters failure should update state to Error`() = runTest {
        val exception = Exception("Network error")
        fakeCharactersRepository.getCharactersResult = Result.failure(exception)

        viewModel.searchCharacters(page = 1, name = "Rick")
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.state is UiState.Error)
        assertEquals(exception, (state.state as UiState.Error).value)
    }

    private class FakeCharactersRepository : CharactersRepository {
        var getCharactersResult: Result<CharactersModel> = Result.failure(Exception())
        override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> = getCharactersResult
        override suspend fun getCharacter(id: Int): Result<CharacterModel> = Result.failure(Exception())
        override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> = Result.failure(Exception())
    }
}
