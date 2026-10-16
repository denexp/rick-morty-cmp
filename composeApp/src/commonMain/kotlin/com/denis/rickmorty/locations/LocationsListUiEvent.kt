package com.denis.rickmorty.locations

sealed class LocationsListUiEvent {
    data class LoadMore(val page: Int, val name: String? = null): LocationsListUiEvent()
    data class SearchLocations(val nextPage: Boolean = false, val name: String? = null) : LocationsListUiEvent()
    data class ToggleLocation(val locationId: Int): LocationsListUiEvent()
    data class CharacterDetail(val id: Int): LocationsListUiEvent()
}
