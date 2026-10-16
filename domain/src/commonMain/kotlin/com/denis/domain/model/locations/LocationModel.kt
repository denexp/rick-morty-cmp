package com.denis.domain.model.locations

data class LocationModel(
    val id: Int = -1,
    val name: String = "",
    val type: String = "",
    val dimension: String = "",
    val residents: List<String> = emptyList(),
    val url: String = "",
    val created: String = "",
)
