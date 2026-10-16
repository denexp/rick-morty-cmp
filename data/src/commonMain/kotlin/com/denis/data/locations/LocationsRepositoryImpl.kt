package com.denis.data.locations

import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import com.denis.domain.PreferencesHelper
import com.denis.domain.get
import com.denis.domain.locations.LocationsRepository
import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel
import kotlinx.serialization.json.Json

class LocationsRepositoryImpl(
    private val api: LocationsApi,
    private val mapper: LocationsMapper,
    private val preferencesHelper: PreferencesHelper
) : LocationsRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getLocations(
        page: Int,
        name: String?,
        type: String?,
        dimension: String?
    ): Result<LocationsModel> {
        val cachedLocations = if (name == null && type == null && dimension == null) getCachedLocations(page) else null
        return if (cachedLocations?.results.isNullOrEmpty()) {
            api.getLocations(page, name, type, dimension).map { entity ->
                if (name == null && type == null && dimension == null) saveLocations(entity, page)
                entity.results.forEach { saveLocation(it) }
                mapper.toModel(entity)
            }
        } else {
            Result.success(mapper.toModel(cachedLocations))
        }
    }

    override suspend fun getLocation(id: Int): Result<LocationModel> {
        val cached = getCachedLocation(id)
        if (cached != null) return Result.success(mapper.toModel(cached))

        return api.getLocation(id).map { entity ->
            saveLocation(entity)
            mapper.toModel(entity)
        }
    }

    override suspend fun getLocations(ids: List<Int>): Result<List<LocationModel>> {
        val cachedLocations = ids.mapNotNull { getCachedLocation(it) }
        if (cachedLocations.size == ids.size) {
            return Result.success(cachedLocations.map { mapper.toModel(it) })
        }

        return api.getLocations(ids).map { list ->
            list.forEach { saveLocation(it) }
            list.map { mapper.toModel(it) }
        }
    }

    private suspend fun saveLocation(location: LocationEntity) {
        preferencesHelper.set(locationKey(location.id), location)
    }

    private suspend fun saveLocations(entity: LocationsEntity, page: Int) {
        preferencesHelper.set(locationsKey(page), entity)
    }

    private suspend fun getCachedLocation(id: Int): LocationEntity? {
        return preferencesHelper.get<LocationEntity>(locationKey(id)).getOrNull()
    }

    private suspend fun getCachedLocations(page: Int): LocationsEntity? {
        return preferencesHelper.get<LocationsEntity>(locationsKey(page)).getOrNull()
    }

    private fun locationsKey(page: Int) = "locations_$page"
    private fun locationKey(id: Int) = "location_$id"
}
