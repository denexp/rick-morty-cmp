package com.denis.data.characters

import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharacterLocationEntity
import com.denis.data.entity.CharactersEntity
import com.denis.data.entity.GenderEntity
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.OriginEntity
import com.denis.data.entity.StatusEntity
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel
import com.denis.domain.model.characters.Gender
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.characters.LocationModel
import com.denis.domain.model.characters.OriginModel
import com.denis.domain.model.characters.Status

open class CharactersMapper {
    open fun toModel(entity: CharactersEntity) =
        CharactersModel(
            info = entity.info.toModel(),
            results = entity.results.map { toModel(it) }
        )
    open fun toModel(entity: CharacterEntity) =
        CharacterModel(
            id = entity.id,
            name = entity.name,
            status = entity.status.toModel(),
            species = entity.species,
            type = entity.type,
            gender = entity.gender.toModel(),
            origin = entity.origin.toModel(),
            location = entity.location.toModel(),
            image = entity.image,
            episodeIds = entity.episode?.map { it.split("/").last().toInt() } ?: emptyList()
        )
    private fun StatusEntity.toModel() = Status.valueOf(name)
    private fun GenderEntity.toModel() = Gender.valueOf(name)
    private fun OriginEntity.toModel() = OriginModel(name, url)
    private fun CharacterLocationEntity.toModel() = LocationModel(name, url)
    private fun InfoEntity.toModel() = InfoModel(count, pages, next.orEmpty(), prev)
}