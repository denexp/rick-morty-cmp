package com.denis.domain.model.episodes

data class EpisodeModel(
    val id: Int = -1,
    val name: String = "",
    val airDate: String = "",
    val episode: String = "",
    val characters: List<String> = emptyList(),
    val url: String = "",
    val created: String = "",
)
