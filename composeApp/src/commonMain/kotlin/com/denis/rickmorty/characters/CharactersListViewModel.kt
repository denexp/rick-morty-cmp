package com.denis.rickmorty.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.domain.characters.LoadCharactersUseCase
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.characters.CharactersModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CharactersListViewModel(private val loadCharactersUseCase: LoadCharactersUseCase): ViewModel() {
    private val _data: MutableStateFlow<CharactersListUiData> = MutableStateFlow(CharactersListUiData())
    val data = _data.asStateFlow()
    private var searchJob: Job? = null

    fun searchCharacters(page: Int = 1, name: String? = null) {
        if (name == null && page == 1 && _data.value.charactersMap.isNotEmpty()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val query = name ?: _data.value.searchQuery
            
            if (name != null) {
                if (page == 1) {
                    _data.update { it.copy(searchQuery = name) }
                }
            } else if (page == 1) {
                _data.update { it.copy(charactersMap = if (it.searchQuery.isEmpty()) it.charactersMap else emptyMap()) }
            }
            
            getCharacters(page, query)
        }
    }

    private suspend fun getCharacters(page: Int = 1, name: String? = null) {
        loadCharactersUseCase.getCharacters(page, name?.ifEmpty { null }) {
            fold(
                onSuccess = { result ->
                    _data.update {
                        it.copy(
                            charactersInfo = result.info,
                            charactersMap = if (page == 1) mapOf(page to result.results) else addResultsToCharactersMap(it, page, result),
                            page = page,
                            state = UiState.Content,
                            searchQuery = name ?: ""
                        )
                    }
                },
                onFailure = { fail ->
                    _data.update { it.copy(state = UiState.Error(fail)) }
                }
            )
        }
    }

    private fun addResultsToCharactersMap(
        data: CharactersListUiData,
        page: Int,
        result: CharactersModel
    ): Map<Int, List<CharacterModel>> = data.charactersMap.toMutableMap().also { map ->
        map[page] = result.results
    }
}
