package com.denis.data.locations

import com.denis.data.entity.InfoEntity
import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.locations.LocationModel
import com.denis.domain.model.locations.LocationsModel

open class LocationsMapper {
    open fun toModel(entity: LocationsEntity) = LocationsModel(
        info = entity.info.toModel(),
        results = entity.results.map { toModel(it) }
    )

    open fun toModel(entity: LocationEntity) = LocationModel(
        id = entity.id,
        name = entity.name,
        type = entity.type,
        dimension = entity.dimension,
        residents = entity.residents,
        url = entity.url,
        created = entity.created
    )

    private fun InfoEntity.toModel() = InfoModel(count, pages, next.orEmpty(), prev)
}
