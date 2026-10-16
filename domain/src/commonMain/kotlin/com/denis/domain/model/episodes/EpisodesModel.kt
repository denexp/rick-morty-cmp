package com.denis.domain.model.episodes

import com.denis.domain.model.characters.InfoModel

data class EpisodesModel(
    val info: InfoModel = InfoModel(),
    val results: List<EpisodeModel> = emptyList(),
)
