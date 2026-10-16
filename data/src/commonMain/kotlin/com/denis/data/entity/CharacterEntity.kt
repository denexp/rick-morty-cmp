package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class CharacterEntity(
    val id: Int,
    val name: String,
    val status: StatusEntity,
    val species: String,
    val type: String,
    val gender: GenderEntity,
    val origin: OriginEntity,
    val location: CharacterLocationEntity,
    val image: String,
    val episode: List<String>?,
)

@Serializable
enum class StatusEntity {
    Alive, Dead, unknown
}

@Serializable
enum class GenderEntity {
    Female, Male, Genderless, unknown
}

@Serializable
data class OriginEntity(
    val name: String,
    val url: String,
)

@Serializable
data class CharacterLocationEntity(
    val name: String,
    val url: String,
)

