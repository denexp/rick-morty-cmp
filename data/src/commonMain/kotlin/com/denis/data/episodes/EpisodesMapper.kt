package com.denis.data.episodes

import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.data.entity.InfoEntity
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.domain.model.episodes.EpisodesModel

open class EpisodesMapper {
    open fun toModel(entity: EpisodesEntity) = EpisodesModel(
        info = entity.info.toModel(),
        results = entity.results.map { toModel(it) }
    )

    open fun toModel(entity: EpisodeEntity) = EpisodeModel(
        id = entity.id,
        name = entity.name,
        airDate = entity.air_date,
        episode = entity.episode,
        characters = entity.characters,
        url = entity.url,
        created = entity.created
    )

    private fun InfoEntity.toModel() = InfoModel(count, pages, next.orEmpty(), prev)
}
