package com.denis.data.characters

import com.denis.data.FakeNetworkApi
import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharacterLocationEntity
import com.denis.data.entity.CharactersEntity
import com.denis.data.entity.GenderEntity
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.OriginEntity
import com.denis.data.entity.StatusEntity
import kotlinx.serialization.json.Json
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CharactersApiTest {

    private lateinit var fakeNetworkApi: FakeNetworkApi
    private lateinit var api: CharactersApi
    private val json = Json { ignoreUnknownKeys = true }
    private val testJson = CharactersApiTestJson()

    private val characterEntity: CharacterEntity by lazy {
        json.decodeFromString(testJson.getSingleCharacter)
    }

    @BeforeTest
    fun setUp() {
        fakeNetworkApi = FakeNetworkApi()
        api = CharactersApi(fakeNetworkApi)
    }

    @Test
    fun `getCharacters should call correct url and return data`() = runTest {
        val page = 1
        val name = "Rick"
        val expectedEntity = CharactersEntity(InfoEntity(1, 1), listOf(characterEntity))
        
        fakeNetworkApi.queueGet<CharactersEntity>(
            url = "api/character",
            query = mapOf("page" to page, "name" to name),
            result = Result.success(expectedEntity)
        )

        val result = api.getCharacters(page, name)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
    }

    @Test
    fun `getCharacter by id should call correct url`() = runTest {
        val id = 1
        fakeNetworkApi.queueGet<CharacterEntity>(
            url = "api/character/$id",
            query = emptyMap(),
            result = Result.success(characterEntity)
        )

        val result = api.getCharacter(id)

        assertTrue(result.isSuccess)
        assertEquals(characterEntity, result.getOrNull())
    }

    @Test
    fun `getCharacters by ids should call correct url for multiple ids`() = runTest {
        val ids = listOf(1, 2)
        val expectedList = listOf(characterEntity, characterEntity.copy(id = 2))
        
        fakeNetworkApi.queueGet<List<CharacterEntity>>(
            url = "api/character/1,2",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getCharacters(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
    }

    @Test
    fun `getCharacters by ids should call correct url for single id`() = runTest {
        val ids = listOf(1)
        fakeNetworkApi.queueGet<CharacterEntity>(
            url = "api/character/1",
            query = emptyMap(),
            result = Result.success(characterEntity)
        )

        val result = api.getCharacters(ids)

        assertTrue(result.isSuccess)
        assertEquals(listOf(characterEntity), result.getOrNull())
    }

    @Test
    fun `getCharacters with getAllCharacters JSON should return full data`() = runTest {
        val page = 1
        val expectedEntity = json.decodeFromString<CharactersEntity>(testJson.getAllCharacters)
        
        fakeNetworkApi.queueGet<CharactersEntity>(
            url = "api/character",
            query = mapOf("page" to page, "name" to null),
            result = Result.success(expectedEntity)
        )

        val result = api.getCharacters(page, null)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
        assertEquals(826, result.getOrNull()?.info?.count)
        assertEquals("Rick Sanchez", result.getOrNull()?.results?.first()?.name)
    }

    @Test
    fun `getCharacters by ids with getMultipleCharacters JSON should return list`() = runTest {
        val ids = listOf(1, 183)
        val expectedList = json.decodeFromString<List<CharacterEntity>>(testJson.getMultipleCharacters)
        
        fakeNetworkApi.queueGet<List<CharacterEntity>>(
            url = "api/character/1,183",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getCharacters(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Johnny Depp", result.getOrNull()?.last()?.name)
    }
}
