package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeEntity(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String,
)
