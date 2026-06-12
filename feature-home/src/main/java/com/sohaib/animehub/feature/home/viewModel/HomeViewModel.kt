package com.sohaib.animehub.feature.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sohaib.animehub.domain.useCases.GetAnimeListUseCase
import com.sohaib.animehub.domain.useCases.ObserveFavouriteAnimeIdsUseCase
import com.sohaib.animehub.domain.useCases.ToggleFavouriteAnimeUseCase
import com.sohaib.animehub.feature.home.effect.HomeEffect
import com.sohaib.animehub.feature.home.intent.HomeIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    getAnimeListUseCase: GetAnimeListUseCase,
    observeFavouriteAnimeIdsUseCase: ObserveFavouriteAnimeIdsUseCase,
    private val toggleFavouriteAnimeUseCase: ToggleFavouriteAnimeUseCase,
) : ViewModel() {

    val animePagingFlow = getAnimeListUseCase().cachedIn(viewModelScope)
    private val _favouriteAnimeIds = MutableStateFlow<Set<String>>(emptySet())
    val favouriteAnimeIds: StateFlow<Set<String>> = _favouriteAnimeIds.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            observeFavouriteAnimeIdsUseCase().collect { favouriteIds ->
                _favouriteAnimeIds.value = favouriteIds
            }
        }
    }

    fun handleIntent(intent: HomeIntent) = viewModelScope.launch {
        when (intent) {
            HomeIntent.Refresh -> _effect.emit(HomeEffect.RefreshAnimeList)
            is HomeIntent.OnItemClick -> _effect.emit(HomeEffect.NavigateToDetailPage(intent.animeId))
            is HomeIntent.ToggleFavourite -> toggleFavouriteAnimeUseCase(intent.anime)
        }
    }
}