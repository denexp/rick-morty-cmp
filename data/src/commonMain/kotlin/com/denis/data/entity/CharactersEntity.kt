package com.denis.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class CharactersEntity(
    val info: InfoEntity,
    val results: List<CharacterEntity>,
)

@Serializable
data class InfoEntity(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null,
)