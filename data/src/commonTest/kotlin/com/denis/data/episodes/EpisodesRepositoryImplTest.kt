package com.denis.data.episodes

import com.denis.data.FakeNetworkApi
import com.denis.data.FakePreferencesHelper
import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.data.entity.InfoEntity
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EpisodesRepositoryImplTest {

    private class MockEpisodesMapper : EpisodesMapper() {
        var toEpisodesModelResult: EpisodesModel? = null
        var toEpisodeModelResult: EpisodeModel? = null

        override fun toModel(entity: EpisodesEntity) = toEpisodesModelResult!!
        override fun toModel(entity: EpisodeEntity) = toEpisodeModelResult!!
    }

    private lateinit var networkApi: FakeNetworkApi
    private lateinit var api: EpisodesApi
    private lateinit var mapper: MockEpisodesMapper
    private lateinit var preferencesHelper: FakePreferencesHelper
    private lateinit var repository: EpisodesRepositoryImpl

    private val episodeEntity = EpisodeEntity(
        id = 1,
        name = "Pilot",
        air_date = "December 2, 2013",
        episode = "S01E01",
        characters = emptyList(),
        url = "url",
        created = "date"
    )

    private val episodeModel = EpisodeModel(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = "S01E01",
        characters = emptyList(),
        url = "url",
        created = "date"
    )

    @BeforeTest
    fun setUp() {
        networkApi = FakeNetworkApi()
        api = EpisodesApi(networkApi)
        mapper = MockEpisodesMapper()
        preferencesHelper = FakePreferencesHelper()
        repository = EpisodesRepositoryImpl(api, mapper, preferencesHelper)
    }

    @Test
    fun `getEpisodes returns from api and saves to cache when cache is empty`() = runTest {
        val page = 1
        val entity = EpisodesEntity(InfoEntity(1, 1, null, null), listOf(episodeEntity))
        val model = EpisodesModel(InfoModel(1, 1, "", null), listOf(episodeModel))

        networkApi.queueGet<EpisodesEntity>("api/episode", mapOf("page" to page, "name" to null), Result.success(entity))
        mapper.toEpisodesModelResult = model
        mapper.toEpisodeModelResult = episodeModel

        val result = repository.getEpisodes(page, null)

        assertTrue(result.isSuccess)
        assertEquals(model, result.getOrNull())

        // Verify caching
        val cachedResult = repository.getEpisodes(page, null)
        assertTrue(cachedResult.isSuccess)
        assertEquals(model, cachedResult.getOrNull())
    }

    @Test
    fun `getEpisode returns from api and saves to cache`() = runTest {
        val id = 1
        networkApi.queueGet<EpisodeEntity>("api/episode/$id", emptyMap(), Result.success(episodeEntity))
        mapper.toEpisodeModelResult = episodeModel

        val result = repository.getEpisode(id)

        assertTrue(result.isSuccess)
        assertEquals(episodeModel, result.getOrNull())

        // Verify caching
        val cachedResult = repository.getEpisode(id)
        assertTrue(cachedResult.isSuccess)
        assertEquals(episodeModel, cachedResult.getOrNull())
    }

    @Test
    fun `getEpisodes by ids returns from api and saves to cache`() = runTest {
        val ids = listOf(1)
        networkApi.queueGet<EpisodeEntity>("api/episode/1", emptyMap(), Result.success(episodeEntity))
        mapper.toEpisodeModelResult = episodeModel

        val result = repository.getEpisodes(ids)

        assertTrue(result.isSuccess)
        assertEquals(listOf(episodeModel), result.getOrNull())

        // Verify caching
        val cachedResult = repository.getEpisodes(ids)
        assertTrue(cachedResult.isSuccess)
        assertEquals(listOf(episodeModel), cachedResult.getOrNull())
    }
}
