package com.denis.data.episodes

import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.domain.PreferencesHelper
import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.get
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel

class EpisodesRepositoryImpl(
    private val api: EpisodesApi,
    private val mapper: EpisodesMapper,
    private val preferencesHelper: PreferencesHelper
) : EpisodesRepository {
    override suspend fun getEpisodes(page: Int, name: String?): Result<EpisodesModel> {
        val cachedEpisodes = if (name == null) getCachedEpisodes(page) else null
        return if (cachedEpisodes?.results.isNullOrEmpty()) {
            api.getEpisodes(page, name).map { entity ->
                if (name == null) saveEpisodes(entity, page)
                entity.results.forEach { saveEpisode(it) }
                mapper.toModel(entity)
            }
        } else {
            Result.success(mapper.toModel(cachedEpisodes))
        }
    }

    override suspend fun getEpisode(id: Int): Result<EpisodeModel> {
        val cached = getCachedEpisode(id)
        if (cached != null) return Result.success(mapper.toModel(cached))

        return api.getEpisode(id).map { entity ->
            saveEpisode(entity)
            mapper.toModel(entity)
        }
    }

    override suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeModel>> {
        val cachedEpisodes = ids.mapNotNull { getCachedEpisode(it) }
        if (cachedEpisodes.size == ids.size) {
            return Result.success(cachedEpisodes.map { mapper.toModel(it) })
        }

        return api.getEpisodes(ids).map { list ->
            list.forEach { saveEpisode(it) }
            list.map { mapper.toModel(it) }
        }
    }

    private suspend fun saveEpisode(episode: EpisodeEntity) {
        preferencesHelper.set(episodeKey(episode.id), episode)
    }

    private suspend fun saveEpisodes(entity: EpisodesEntity, page: Int) {
        preferencesHelper.set(episodesKey(page), entity)
    }

    private suspend fun getCachedEpisode(id: Int): EpisodeEntity? {
        return preferencesHelper.get<EpisodeEntity>(episodeKey(id)).getOrNull()
    }

    private suspend fun getCachedEpisodes(page: Int): EpisodesEntity? {
        return preferencesHelper.get<EpisodesEntity>(episodesKey(page)).getOrNull()
    }

    private fun episodesKey(page: Int) = "episodes_$page"
    private fun episodeKey(id: Int) = "episode_$id"
}
