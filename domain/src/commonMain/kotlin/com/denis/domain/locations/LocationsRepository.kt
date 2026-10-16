package com.denis.domain.locations

import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel

interface LocationsRepository {
    suspend fun getLocations(page: Int = 1, name: String? = null, type: String? = null, dimension: String? = null): Result<LocationsModel>
    suspend fun getLocation(id: Int): Result<LocationModel>
    suspend fun getLocations(ids: List<Int>): Result<List<LocationModel>>
}
