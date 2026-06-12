package com.sohaib.animehub.feature.favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohaib.animehub.domain.useCases.GetFavouriteAnimeListUseCase
import com.sohaib.animehub.domain.useCases.ToggleFavouriteAnimeUseCase
import com.sohaib.animehub.feature.favourites.intent.FavouriteIntent
import com.sohaib.animehub.feature.favourites.state.FavouriteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val getFavouriteAnimeListUseCase: GetFavouriteAnimeListUseCase,
    private val toggleFavouriteAnimeUseCase: ToggleFavouriteAnimeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavouriteState())
    val state: StateFlow<FavouriteState> = _state.asStateFlow()

    init {
        handleIntent(FavouriteIntent.GetFavouriteList)
    }

    fun handleIntent(intent: FavouriteIntent) = viewModelScope.launch {
        when (intent) {
            is FavouriteIntent.GetFavouriteList -> observeFavouriteList()
            is FavouriteIntent.ToggleFavourite -> toggleFavouriteAnimeUseCase(intent.anime)
        }
    }

    private suspend fun observeFavouriteList() {
        getFavouriteAnimeListUseCase().collect { animeList ->
            _state.update { it.copy(favouriteAnimeList = animeList) }
        }
    }
}