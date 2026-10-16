package com.denis.domain.model.characters

data class CharactersModel(
    val info: InfoModel = InfoModel(),
    val results: List<CharacterModel> = emptyList(),
)

data class InfoModel(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String = "",
    val prev: String? = null,
)