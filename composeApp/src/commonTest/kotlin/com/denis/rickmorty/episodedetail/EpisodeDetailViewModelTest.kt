package com.denis.rickmorty.episodedetail

import com.denis.domain.episodes.LoadEpisodeDetailUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.characters.CharactersRepository
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
class EpisodeDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeEpisodesRepository: FakeEpisodesRepository
    private lateinit var fakeCharactersRepository: FakeCharactersRepository
    private lateinit var loadEpisodeDetailUseCase: LoadEpisodeDetailUseCase
    private lateinit var viewModel: EpisodeDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeEpisodesRepository = FakeEpisodesRepository()
        fakeCharactersRepository = FakeCharactersRepository()
        loadEpisodeDetailUseCase = LoadEpisodeDetailUseCase(fakeEpisodesRepository, fakeCharactersRepository)
        viewModel = EpisodeDetailViewModel(loadEpisodeDetailUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `episode detail success should update state to Content`() = runTest {
        val episodeId = 1
        val characterUrls = listOf("https://rickandmortyapi.com/api/character/1")
        val episode = EpisodeModel(id = episodeId, name = "Pilot", characters = characterUrls)
        val characters = listOf(CharacterModel(id = 1, name = "Rick Sanchez"))

        fakeEpisodesRepository.getEpisodeResult = Result.success(episode)
        fakeCharactersRepository.getCharactersByIdsResult = Result.success(characters)

        viewModel.loadEpisode(episodeId)
        advanceUntilIdle()

        val state = viewModel.data.value
        assertEquals(UiState.Content, state.state)
        assertEquals(episode, state.episode)
        assertEquals(characters, state.characters)
    }

    @Test
    fun `episode detail failure should update state to Error`() = runTest {
        val episodeId = 1
        val exception = Exception("Episode not found")
        fakeEpisodesRepository.getEpisodeResult = Result.failure(exception)

        viewModel.loadEpisode(episodeId)
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.state is UiState.Error)
        assertEquals(exception, (state.state as UiState.Error).value)
    }

    private class FakeEpisodesRepository : EpisodesRepository {
        var getEpisodeResult: Result<EpisodeModel> = Result.failure(Exception())
        override suspend fun getEpisodes(page: Int, name: String?): Result<EpisodesModel> = Result.failure(Exception())
        override suspend fun getEpisode(id: Int): Result<EpisodeModel> = getEpisodeResult
        override suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>> = Result.failure(Exception())
    }

    private class FakeCharactersRepository : CharactersRepository {
        var getCharactersByIdsResult: Result<List<CharacterModel>> = Result.failure(Exception())
        override suspend fun getCharacters(page: Int, name: String?): Result<CharactersModel> = Result.failure(Exception())
        override suspend fun getCharacter(id: Int): Result<CharacterModel> = Result.failure(Exception())
        override suspend fun getCharacters(ids: List<Int>): Result<List<CharacterModel>> = getCharactersByIdsResult
    }
}
