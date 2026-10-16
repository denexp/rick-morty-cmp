package com.denis.rickmorty.locations

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.InfoModel
import com.denis.domain.model.locations.LocationModel

data class LocationsListUiData(
    val locationsInfo: InfoModel = InfoModel(),
    val locations: List<LocationModel> = emptyList(),
    val residents: Map<Int, List<CharacterModel>> = emptyMap(),
    val expandedLocations: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val page: Int = 1,
    val state: UiState = UiState.Loading,
    val loadingMore: Boolean = false,
    val scrollState: MutableState<Int> = mutableIntStateOf(0),
)
