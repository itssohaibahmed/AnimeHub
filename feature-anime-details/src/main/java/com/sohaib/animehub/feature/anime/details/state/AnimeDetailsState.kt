package com.sohaib.animehub.feature.anime.details.state

import com.sohaib.animehub.domain.models.AnimeDetail

data class AnimeDetailsState(
    val uiState: AnimeDetailsUiState = AnimeDetailsUiState.Loading,
    val isRefreshing: Boolean = false,
    val hasCompletedInitialSync: Boolean = false,
    val isFavourite: Boolean = false,
)

sealed interface AnimeDetailsUiState {
    data object Loading : AnimeDetailsUiState
    data object Empty : AnimeDetailsUiState
    data class Error(val messageResId: Int) : AnimeDetailsUiState
    data class Success(val animeDetail: AnimeDetail) : AnimeDetailsUiState
}