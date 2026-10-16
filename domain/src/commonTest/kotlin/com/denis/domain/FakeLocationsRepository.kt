package com.denis.domain

import com.denis.domain.locations.LocationsRepository
import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel

class FakeLocationsRepository : LocationsRepository {
    var getLocationsResult: Result<LocationsModel> = Result.failure(Exception("Not implemented"))
    var getLocationResult: Result<LocationModel> = Result.failure(Exception("Not implemented"))
    var getLocationsByIdsResult: Result<List<LocationModel>> = Result.failure(Exception("Not implemented"))

    override suspend fun getLocations(
        page: Int,
        name: String?,
        type: String?,
        dimension: String?
    ): Result<LocationsModel> = getLocationsResult

    override suspend fun getLocation(id: Int): Result<LocationModel> = getLocationResult

    override suspend fun getLocations(ids: List<Int>): Result<List<LocationModel>> = getLocationsByIdsResult
}
