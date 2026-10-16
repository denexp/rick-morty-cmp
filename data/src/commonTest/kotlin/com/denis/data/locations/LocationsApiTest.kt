package com.denis.data.locations

import com.denis.data.FakeNetworkApi
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import kotlinx.serialization.json.Json
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationsApiTest {

    private lateinit var fakeNetworkApi: FakeNetworkApi
    private lateinit var api: LocationsApi
    private val json = Json { ignoreUnknownKeys = true }
    private val testJson = LocationApiTestJson()

    private val locationEntity: LocationEntity by lazy {
        json.decodeFromString(testJson.getSingleLocation)
    }

    @BeforeTest
    fun setUp() {
        fakeNetworkApi = FakeNetworkApi()
        api = LocationsApi(fakeNetworkApi)
    }

    @Test
    fun `getLocations should call correct url and return data`() = runTest {
        val page = 1
        val name = "Earth"
        val type = "Planet"
        val expectedEntity = LocationsEntity(InfoEntity(1, 1), listOf(locationEntity))
        
        fakeNetworkApi.queueGet<LocationsEntity>(
            url = "api/location",
            query = mapOf("page" to page, "name" to name, "type" to type),
            result = Result.success(expectedEntity)
        )

        val result = api.getLocations(page, name, type, null)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
    }

    @Test
    fun `getLocation by id should call correct url`() = runTest {
        val id = 1
        fakeNetworkApi.queueGet<LocationEntity>(
            url = "api/location/$id",
            query = emptyMap(),
            result = Result.success(locationEntity)
        )

        val result = api.getLocation(id)

        assertTrue(result.isSuccess)
        assertEquals(locationEntity, result.getOrNull())
    }

    @Test
    fun `getLocations by ids should call correct url for multiple ids`() = runTest {
        val ids = listOf(1, 2)
        val expectedList = listOf(locationEntity, locationEntity.copy(id = 2))
        
        fakeNetworkApi.queueGet<List<LocationEntity>>(
            url = "api/location/1,2",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getLocations(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
    }

    @Test
    fun `getLocations with getAllLocations JSON should return full data`() = runTest {
        val page = 1
        val expectedEntity = json.decodeFromString<LocationsEntity>(testJson.getAllLocations)
        
        fakeNetworkApi.queueGet<LocationsEntity>(
            url = "api/location",
            query = mapOf("page" to page),
            result = Result.success(expectedEntity)
        )

        val result = api.getLocations(page, null, null, null)

        assertTrue(result.isSuccess)
        assertEquals(expectedEntity, result.getOrNull())
        assertEquals(126, result.getOrNull()?.info?.count)
        assertEquals("Earth", result.getOrNull()?.results?.first()?.name)
    }

    @Test
    fun `getLocations by ids with getMultipleLocations JSON should return list`() = runTest {
        val ids = listOf(3, 21)
        val expectedList = json.decodeFromString<List<LocationEntity>>(testJson.getMultipleLocations)
        
        fakeNetworkApi.queueGet<List<LocationEntity>>(
            url = "api/location/3,21",
            query = emptyMap(),
            result = Result.success(expectedList)
        )

        val result = api.getLocations(ids)

        assertTrue(result.isSuccess)
        assertEquals(expectedList, result.getOrNull())
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Testicle Monster Dimension", result.getOrNull()?.last()?.name)
    }
}
