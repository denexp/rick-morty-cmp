package com.denis.domain.episodes

import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel

interface EpisodesRepository {
    suspend fun getEpisodes(page: Int = 1, name: String? = null): Result<EpisodesModel>
    suspend fun getEpisode(id: Int): Result<EpisodeModel>
    suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>>
}
