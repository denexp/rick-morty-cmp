package com.denis.domain

import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel

class FakeEpisodesRepository : EpisodesRepository {
    var getEpisodesResult: Result<EpisodesModel> = Result.failure(Exception("Not implemented"))
    var getEpisodeResult: Result<EpisodeModel> = Result.failure(Exception("Not implemented"))
    var getEpisodesListResult: Result<List<EpisodeModel>> = Result.failure(Exception("Not implemented"))

    override suspend fun getEpisodes(page: Int, name: String?): Result<EpisodesModel> = getEpisodesResult
    override suspend fun getEpisode(id: Int): Result<EpisodeModel> = getEpisodeResult
    override suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>> = getEpisodesListResult
}
