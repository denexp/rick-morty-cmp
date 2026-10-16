package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class LocationsEntity(
    val info: InfoEntity,
    val results: List<LocationEntity>,
)
