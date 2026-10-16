package com.denis.domain.model.characters

data class CharacterModel(
    val id: Int = -1,
    val name: String = "",
    val status: Status = Status.unknown,
    val species: String = "",
    val type: String = "",
    val gender: Gender = Gender.unknown,
    val origin: OriginModel = OriginModel(),
    val location: LocationModel = LocationModel(),
    val image: String = "",
    val episodeIds: List<Int> = emptyList(),
)

enum class Status {
    Alive, Dead, unknown
}

enum class Gender {
    Female, Male, Genderless, unknown
}

data class OriginModel(
    val name: String = "",
    val url: String = "",
)

data class LocationModel(
    val name: String = "",
    val url: String = "",
)