package com.denis.domain.model

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object LandingScreen: Destination()
    @Serializable
    data object Characters: Destination()
    @Serializable
    data class CharacterDetail(val characterId: Int): Destination()
    @Serializable
    data class EpisodeDetail(val episodeId: Int): Destination()
}