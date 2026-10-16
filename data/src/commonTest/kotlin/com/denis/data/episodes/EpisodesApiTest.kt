package com.denis.data.episodes

import com.denis.data.FakeNetworkApi
import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.data.entity.InfoEntity
import kotlinx.serialization.json.Json
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EpisodesApiTest {

    private lateinit var fakeNetworkApi: FakeNetworkApi
    private lateinit var api: EpisodesApi
    private val json = Json { ignoreUnknownKeys = true }
    private val testJson = EpisodesApiTestJson()

    private val episodeEntity: EpisodeEntity by lazy {
        json.decodeFromString(testJson.getSingleEpisode)
    }

    @BeforeTest
    fun setUp() {
        fakeNetworkApi = FakeNetworkApi()
        api = EpisodesApi(fakeNetworkApi)
    }

    @Test
    fun `getEpisodes should call correct url and return data`() = runTest {
        val page = 1
        val name = "Pilot"
        val expectedEntity = EpisodesEntity(InfoEntity(1, 1), listOf(episodeEntity))
        
        fakeNetworkApi.queueGet<EpisodesEntity>(
            url = "api/episode",
            query = mapOf("page" to page, "name" to name),
            result = Result.success(expectedEntity)
        )

        val result = api.getEpisodes(page, name)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
    }

    @Test
    fun `getEpisode by id should call correct url`() = runTest {
        val id = 1
        fakeNetworkApi.queueGet<EpisodeEntity>(
            url = "api/episode/$id",
            query = emptyMap(),
            result = Result.success(episodeEntity)
        )

        val result = api.getEpisode(id)

        assertTrue(result.isSuccess)
        assertEquals(episodeEntity, result.getOrNull())
    }

    @Test
    fun `getEpisodes by ids should call correct url for multiple ids`() = runTest {
        val ids = listOf(1, 2)
        val expectedList = listOf(episodeEntity, episodeEntity.copy(id = 2))
        
        fakeNetworkApi.queueGet<List<EpisodeEntity>>(
            url = "api/episode/1,2",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getEpisodes(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
    }

    @Test
    fun `getEpisodes with getAllEpisodes JSON should return full data`() = runTest {
        val page = 1
        val expectedEntity = json.decodeFromString<EpisodesEntity>(testJson.getAllEpisodes)
        
        fakeNetworkApi.queueGet<EpisodesEntity>(
            url = "api/episode",
            query = mapOf("page" to page, "name" to null),
            result = Result.success(expectedEntity)
        )

        val result = api.getEpisodes(page, null)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
        assertEquals(51, result.getOrNull()?.info?.count)
        assertEquals("Pilot", result.getOrNull()?.results?.first()?.name)
    }

    @Test
    fun `getEpisodes by ids with getMultipleEpisodes JSON should return list`() = runTest {
        val ids = listOf(10, 28)
        val expectedList = json.decodeFromString<List<EpisodeEntity>>(testJson.getMultipleEpisodes)
        
        fakeNetworkApi.queueGet<List<EpisodeEntity>>(
            url = "api/episode/10,28",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getEpisodes(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("The Ricklantis Mixup", result.getOrNull()?.last()?.name)
    }
}
