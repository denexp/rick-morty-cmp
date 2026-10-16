package com.denis.rickmorty.episodes

import com.denis.domain.episodes.LoadEpisodesUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.episodes.EpisodesRepository
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
class EpisodesListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeEpisodesRepository: FakeEpisodesRepository
    private lateinit var loadEpisodesUseCase: LoadEpisodesUseCase
    private lateinit var viewModel: EpisodesListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeEpisodesRepository = FakeEpisodesRepository()
        loadEpisodesUseCase = LoadEpisodesUseCase(fakeEpisodesRepository)
        viewModel = EpisodesListViewModel(loadEpisodesUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEpisodes success should update state to Content`() = runTest {
        val episodes = listOf(EpisodeModel(id = 1, name = "Pilot"))
        val episodesModel = EpisodesModel(info = InfoModel(count = 1, pages = 1), results = episodes)
        fakeEpisodesRepository.getEpisodesResult = Result.success(episodesModel)

        viewModel.loadEpisodes()
        advanceUntilIdle()

        val state = viewModel.data.value
        assertEquals(UiState.Content, state.state)
        assertEquals(episodes, state.episodesMap[1])
    }

    @Test
    fun `loadEpisodes failure should update state to Error if no data`() = runTest {
        val exception = Exception("Network error")
        fakeEpisodesRepository.getEpisodesResult = Result.failure(exception)

        viewModel.loadEpisodes()
        advanceUntilIdle()

        val state = viewModel.data.value
        assertTrue(state.state is UiState.Error)
        assertEquals(exception, (state.state as UiState.Error).value)
    }

    @Test
    fun `toggleSeason should update expandedSeasons`() = runTest {
        val season = "S01"
        
        viewModel.toggleSeason(season)
        assertTrue(viewModel.data.value.expandedSeasons.contains(season))

        viewModel.toggleSeason(season)
        assertTrue(!viewModel.data.value.expandedSeasons.contains(season))
    }

    private class FakeEpisodesRepository : EpisodesRepository {
        var getEpisodesResult: Result<EpisodesModel> = Result.failure(Exception())
        override suspend fun getEpisodes(page: Int, name: String?): Result<EpisodesModel> = getEpisodesResult
        override suspend fun getEpisode(id: Int): Result<EpisodeModel> = Result.failure(Exception())
        override suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>> = Result.failure(Exception())
    }
}
