package com.denis.data.locations

import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import com.denis.data.network.NetworkApi
import com.denis.data.network.get

open class LocationsApi(private val api: NetworkApi) {
    open suspend fun getLocations(page: Int, name: String?, type: String?, dimension: String?) = 
        api.get<LocationsEntity>("api/location", query = buildMap {
            put("page", page)
            name?.let { put("name", it) }
            type?.let { put("type", it) }
            dimension?.let { put("dimension", it) }
        })

    open suspend fun getLocation(id: Int) = api.get<LocationEntity>("api/location/$id")

    open suspend fun getLocations(ids: List<Int>): Result<List<LocationEntity>> {
        if (ids.isEmpty()) return Result.success(emptyList())
        return if (ids.size == 1) {
            api.get<LocationEntity>("api/location/${ids[0]}").map { listOf(it) }
        } else {
            api.get<List<LocationEntity>>("api/location/${ids.joinToString(",")}")
        }
    }
}
