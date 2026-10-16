package com.denis.rickmorty.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.domain.locations.LoadLocationsUseCase
import com.denis.domain.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationsListViewModel(
    private val loadLocationsUseCase: LoadLocationsUseCase
) : ViewModel() {
    private val _data = MutableStateFlow(LocationsListUiData())
    val data = _data.asStateFlow()

    fun loadLocations(page: Int = 1, name: String? = null) {
        if (page == 1 && name == null && _data.value.locations.isNotEmpty()) return
        if (page > 1 && (_data.value.loadingMore || _data.value.locationsInfo.next.isEmpty())) return

        viewModelScope.launch {
            val query = name ?: _data.value.searchQuery
            if (name != null) {
                if (page == 1) {
                    _data.update { it.copy(searchQuery = name) }
                }
            } else if (page == 1) {
                _data.update { it.copy(locations = if (it.searchQuery.isEmpty()) it.locations else emptyList()) }
            } else {
                _data.update { it.copy(loadingMore = true) }
            }

            loadLocationsUseCase.getLocations(page, query.ifEmpty { null }) {
                fold(
                    onSuccess = { result ->
                        _data.update {
                            it.copy(
                                locationsInfo = result.info,
                                locations = if (page == 1) result.results else it.locations + result.results,
                                page = page,
                                state = UiState.Content,
                                loadingMore = false,
                                searchQuery = query
                            )
                        }
                    },
                    onFailure = { fail ->
                        _data.update { it.copy(state = UiState.Error(fail), loadingMore = false) }
                    }
                )
            }
        }
    }

    fun toggleLocation(locationId: Int) {
        val location = _data.value.locations.find { it.id == locationId } ?: return
        val isExpanded = _data.value.expandedLocations.contains(locationId)

        if (!isExpanded) {
            _data.update { it.copy(expandedLocations = it.expandedLocations + locationId) }
            if (!_data.value.residents.containsKey(locationId) && location.residents.isNotEmpty()) {
                fetchResidents(locationId, location.residents)
            }
        } else {
            _data.update { it.copy(expandedLocations = it.expandedLocations - locationId) }
        }
    }

    private fun fetchResidents(locationId: Int, residentUrls: List<String>) {
        val ids = residentUrls.mapNotNull { url ->
            url.split("/").lastOrNull()?.toIntOrNull()
        }
        if (ids.isEmpty()) return

        viewModelScope.launch {
            loadLocationsUseCase.getResidents(ids) {
                onSuccess { characters ->
                    _data.update {
                        it.copy(residents = it.residents + (locationId to characters))
                    }
                }
            }
        }
    }
}
