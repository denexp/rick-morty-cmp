package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class LocationEntity(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String
)
