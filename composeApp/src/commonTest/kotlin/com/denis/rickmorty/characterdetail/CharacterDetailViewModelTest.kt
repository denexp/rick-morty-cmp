package com.denis.rickmorty.characterdetail

import com.denis.domain.characters.LoadCharacterDetailUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.model.characters.CharactersModel
import com.denis.domain.model.episodes.EpisodesModel
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
class CharacterDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeCharactersRepository: FakeCharactersRepository
    private lateinit var fakeEpisodesRepository: FakeEpisodesRepository
    private lateinit var loadCharacterDetailUseCase: LoadCharacterDetailUseCase
    private lateinit var viewModel: CharacterDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeCharactersRepository = FakeCharactersRepository()
        fakeEpisodesRepository = FakeEpisodesRepository()
        loadCharacterDetailUseCase = LoadCharacterDetailUseCase(fakeCharactersRepository, fakeEpisodesRepository)
        viewModel = CharacterDetailViewModel(loadCharacterDetailUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `character detail success should update state to Content`() = runTest {
        val characterId = 1
        val episodeIds = listOf(1, 2)
        val character = CharacterModel(id = characterId, name = "Rick Sanchez", episodeIds = episodeIds)
        val episodes = listOf(
            EpisodeModel(id = 1, name = "Pilot"),
            EpisodeModel(id = 2, name = "Lawnmower Dog")
        )

        fakeCharactersRepository.getCharacterResult = Result.success(character)
        fakeEpisodesRepository.getEpisodesByIdsResult = Result.success(episodes)

        viewModel.character(characterId)
        advanceUntilIdle()

        val state = viewModel.data.value
        assertEquals(UiState.Content, state.state)
        assertEquals(character, state.character)
        assertEquals(episodes, state.episodes)
    }

    @Test
    fun `character detail failure should update state to Error`() = runTest {
        val characterId = 1
        val exception = Exception("Character not found")
        fakeCharactersRepository.getCharacterResult = Result.failure(exception)

        viewModel.character(characterId)
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.state is UiState.Error)
        assertEquals(exception, (state.state as UiState.Error).value)
    }

    private class FakeCharactersRepository : CharactersRepository {
        var getCharacterResult: Result<CharacterModel> = Result.failure(Exception())
        override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> = Result.failure(Exception())
        override suspend fun getCharacter(id: Int): Result<CharacterModel> = getCharacterResult
        override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> = Result.failure(Exception())
    }

    private class FakeEpisodesRepository : EpisodesRepository {
        var getEpisodesByIdsResult: Result<List<EpisodeModel>> = Result.failure(Exception())
        override suspend fun getEpisodes(page: Int, name: String?): Result<EpisodesModel> = Result.failure(Exception())
        override suspend fun getEpisode(id: Int): Result<EpisodeModel> = Result.failure(Exception())
        override suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>> = getEpisodesByIdsResult
    }
}
