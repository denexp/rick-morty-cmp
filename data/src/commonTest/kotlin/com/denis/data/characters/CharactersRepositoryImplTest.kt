package com.denis.data.characters

import com.denis.data.FakeNetworkApi
import com.denis.data.FakePreferencesHelper
import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharacterLocationEntity
import com.denis.data.entity.CharactersEntity
import com.denis.data.entity.GenderEntity
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.OriginEntity
import com.denis.data.entity.StatusEntity
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel
import com.denis.domain.model.characters.Gender
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.characters.LocationModel
import com.denis.domain.model.characters.OriginModel
import com.denis.domain.model.characters.Status
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CharactersRepositoryImplTest {

    private class MockCharactersMapper : CharactersMapper() {
        var toCharactersModelResult: CharactersModel? = null
        var toCharacterModelResult: CharacterModel? = null

        override fun toModel(entity: CharactersEntity) = toCharactersModelResult!!
        override fun toModel(entity: CharacterEntity) = toCharacterModelResult!!
    }

    private lateinit var networkApi: FakeNetworkApi
    private lateinit var api: CharactersApi
    private lateinit var mapper: MockCharactersMapper
    private lateinit var preferencesHelper: FakePreferencesHelper
    private lateinit var repository: CharactersRepositoryImpl

    private val characterEntity = CharacterEntity(
        id = 1,
        name = "Rick Sanchez",
        status = StatusEntity.Alive,
        species = "Human",
        type = "",
        gender = GenderEntity.Male,
        origin = OriginEntity("Earth", ""),
        location = CharacterLocationEntity("Earth", ""),
        image = "url",
        episode = emptyList(),
    )

    private val characterModel = CharacterModel(
        id = 1,
        name = "Rick Sanchez",
        status = Status.Alive,
        species = "Human",
        type = "",
        gender = Gender.Male,
        origin = OriginModel("Earth", ""),
        location = LocationModel("Earth", ""),
        image = "url",
        episodeIds = emptyList(),
    )

    @BeforeTest
    fun setUp() {
        networkApi = FakeNetworkApi()
        api = CharactersApi(networkApi)
        mapper = MockCharactersMapper()
        preferencesHelper = FakePreferencesHelper()
        repository = CharactersRepositoryImpl(api, mapper, preferencesHelper)
    }

    @Test
    fun `getCharacters returns from api and saves to cache when cache is empty`() = runTest {
        val page = 1
        val entity = CharactersEntity(InfoEntity(1, 1, null, null), listOf(characterEntity))
        val model = CharactersModel(InfoModel(1, 1, "", null), listOf(characterModel))

        networkApi.queueGet<CharactersEntity>("api/character", mapOf("page" to page, "name" to null), Result.success(entity))
        mapper.toCharactersModelResult = model
        mapper.toCharacterModelResult = characterModel

        val result = repository.getCharacters(page, null)

        assertTrue(result.isSuccess)
        assertEquals(model, result.getOrNull())
        
        // Verify caching
        val cachedResult = repository.getCharacters(page, null)
        assertTrue(cachedResult.isSuccess)
        assertEquals(model, cachedResult.getOrNull())
    }

    @Test
    fun `getCharacter returns from api and saves to cache`() = runTest {
        val id = 1
        networkApi.queueGet<CharacterEntity>("api/character/$id", emptyMap(), Result.success(characterEntity))
        mapper.toCharacterModelResult = characterModel

        val result = repository.getCharacter(id)

        assertTrue(result.isSuccess)
        assertEquals(characterModel, result.getOrNull())

        // Verify caching
        val cachedResult = repository.getCharacter(id)
        assertTrue(cachedResult.isSuccess)
        assertEquals(characterModel, cachedResult.getOrNull())
    }

    @Test
    fun `getCharacters by ids returns from api and saves to cache`() = runTest {
        val ids = listOf(1)
        networkApi.queueGet<CharacterEntity>("api/character/1", emptyMap(), Result.success(characterEntity))
        mapper.toCharacterModelResult = characterModel

        val result = repository.getCharacters(ids)

        assertTrue(result.isSuccess)
        assertEquals(listOf(characterModel), result.getOrNull())

        // Verify caching
        val cachedResult = repository.getCharacters(ids)
        assertTrue(cachedResult.isSuccess)
        assertEquals(listOf(characterModel), cachedResult.getOrNull())
    }
}
