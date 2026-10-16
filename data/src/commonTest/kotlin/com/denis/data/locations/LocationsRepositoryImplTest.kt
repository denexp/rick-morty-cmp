package com.denis.data.locations

import com.denis.data.FakeNetworkApi
import com.denis.data.FakePreferencesHelper
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationsRepositoryImplTest {

    private class MockLocationsMapper : LocationsMapper() {
        var toLocationsModelResult: LocationsModel? = null
        var toLocationModelResult: LocationModel? = null

        override fun toModel(entity: LocationsEntity) = toLocationsModelResult!!
        override fun toModel(entity: LocationEntity) = toLocationModelResult!!
    }

    private lateinit var networkApi: FakeNetworkApi
    private lateinit var api: LocationsApi
    private lateinit var mapper: MockLocationsMapper
    private lateinit var preferencesHelper: FakePreferencesHelper
    private lateinit var repository: LocationsRepositoryImpl

    private val locationEntity = LocationEntity(
        id = 1,
        name = "Earth",
        type = "Planet",
        dimension = "C-137",
        residents = emptyList(),
        url = "url",
        created = "date"
    )

    private val locationModel = LocationModel(
        id = 1,
        name = "Earth",
        type = "Planet",
        dimension = "C-137",
        residents = emptyList(),
        url = "url",
        created = "date"
    )

    @BeforeTest
    fun setUp() {
        networkApi = FakeNetworkApi()
        api = LocationsApi(networkApi)
        mapper = MockLocationsMapper()
        preferencesHelper = FakePreferencesHelper()
        repository = LocationsRepositoryImpl(api, mapper, preferencesHelper)
    }

    @Test
    fun `getLocations returns from api and saves to cache when cache is empty`() = runTest {
        val page = 1
        val entity = LocationsEntity(InfoEntity(1, 1, null, null), listOf(locationEntity))
        val model = LocationsModel(InfoModel(1, 1, "", null), listOf(locationModel))

        networkApi.queueGet<LocationsEntity>("api/location", mapOf("page" to page), Result.success(entity))
        mapper.toLocationsModelResult = model
        mapper.toLocationModelResult = locationModel

        val result = repository.getLocations(page, null, null, null)

        assertTrue(result.isSuccess)
        assertEquals(model, result.getOrNull())

        // Verify caching
        val cachedResult = repository.getLocations(1, null, null, null)
        assertTrue(cachedResult.isSuccess)
        assertEquals(model, cachedResult.getOrNull())
    }

    @Test
    fun `getLocation returns from api and saves to cache`() = runTest {
        val id = 1
        networkApi.queueGet<LocationEntity>("api/location/$id", emptyMap(), Result.success(locationEntity))
        mapper.toLocationModelResult = locationModel

        val result = repository.getLocation(id)

        assertTrue(result.isSuccess)
        assertEquals(locationModel, result.getOrNull())

        // Verify caching
        val cachedResult = repository.getLocation(1)
        assertTrue(cachedResult.isSuccess)
        assertEquals(locationModel, cachedResult.getOrNull())
    }

    @Test
    fun `getLocations by ids returns from api and saves to cache`() = runTest {
        val ids = listOf(1)
        networkApi.queueGet<LocationEntity>("api/location/1", emptyMap(), Result.success(locationEntity))
        mapper.toLocationModelResult = locationModel

        val result = repository.getLocations(ids)

        assertTrue(result.isSuccess)
        assertEquals(listOf(locationModel), result.getOrNull())

        // Verify caching
        val cachedResult = repository.getLocations(ids)
        assertTrue(cachedResult.isSuccess)
        assertEquals(listOf(locationModel), cachedResult.getOrNull())
    }
}
