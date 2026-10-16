package com.denis.data.episodes

import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.data.network.NetworkApi
import com.denis.data.network.get

open class EpisodesApi(private val api: NetworkApi) {
    open suspend fun getEpisodes(page: Int, name: String? = null) = api.get<EpisodesEntity>("api/episode", query = mapOf("page" to page, "name" to name))
    open suspend fun getEpisode(id: Int) = api.get<EpisodeEntity>("api/episode/$id")
    open suspend fun getEpisodes(ids: List<Int>): Result<List<EpisodeEntity>> {
        if (ids.isEmpty()) return Result.success(emptyList())
        return if (ids.size == 1) {
            api.get<EpisodeEntity>("api/episode/${ids[0]}").map { listOf(it) }
        } else {
            api.get<List<EpisodeEntity>>("api/episode/${ids.joinToString(",")}")
        }
    }
}
