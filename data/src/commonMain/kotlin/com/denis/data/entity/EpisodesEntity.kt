package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class EpisodesEntity(
    val info: InfoEntity,
    val results: List<EpisodeEntity>,
)
