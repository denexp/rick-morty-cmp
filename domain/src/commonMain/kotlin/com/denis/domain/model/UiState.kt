package com.denis.domain.model

sealed class UiState {
    data object Loading : UiState()
    data object Content : UiState()
    data class Error(val value: Throwable? = null) : UiState()
}