package com.denis.domain.episodes

import com.denis.domain.BaseUseCase
import com.denis.domain.model.episodes.EpisodesModel

class LoadEpisodesUseCase(
    private val repository: EpisodesRepository
) : BaseUseCase() {

    suspend fun getEpisodes(
        page: Int = 1,
        name: String? = null,
        send: Result<EpisodesModel>.() -> Unit = {}
    ): LoadEpisodesUseCase {
        clear()
        return execute {
            val result = repository.getEpisodes(page, name)
            save(result)
            send(result)
        }
    }
}
