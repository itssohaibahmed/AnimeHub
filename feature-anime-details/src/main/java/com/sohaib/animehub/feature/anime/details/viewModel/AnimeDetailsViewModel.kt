package com.sohaib.animehub.feature.anime.details.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohaib.animehub.core.common.constants.Constants.TAG
import com.sohaib.animehub.domain.errors.DomainError
import com.sohaib.animehub.domain.extensions.toAnime
import com.sohaib.animehub.domain.useCases.GetAnimeDetailsByIdUseCase
import com.sohaib.animehub.domain.useCases.ObserveFavouriteAnimeIdsUseCase
import com.sohaib.animehub.domain.useCases.RefreshAnimeDetailsByIdUseCase
import com.sohaib.animehub.domain.useCases.ToggleFavouriteAnimeUseCase
import com.sohaib.animehub.feature.anime.details.effect.AnimeDetailsEffect
import com.sohaib.animehub.feature.anime.details.intent.AnimeDetailsIntent
import com.sohaib.animehub.feature.anime.details.state.AnimeDetailsState
import com.sohaib.animehub.feature.anime.details.state.AnimeDetailsUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.sohaib.animehub.core.common.R as commonR

class AnimeDetailsViewModel(
    private val getUseCase: GetAnimeDetailsByIdUseCase,
    private val refreshUseCase: RefreshAnimeDetailsByIdUseCase,
    private val observeFavouriteAnimeIdsUseCase: ObserveFavouriteAnimeIdsUseCase,
    private val toggleFavouriteAnimeUseCase: ToggleFavouriteAnimeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AnimeDetailsState())
    val state: StateFlow<AnimeDetailsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AnimeDetailsEffect>()
    val effect: SharedFlow<AnimeDetailsEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    private var observeJob: Job? = null
    private var favouriteJob: Job? = null
    private var currentAnimeId: String = ""

    init {
        favouriteJob = viewModelScope.launch {
            observeFavouriteAnimeIdsUseCase().collect { favouriteIds ->
                _state.update { current ->
                    current.copy(isFavourite = currentAnimeId.isNotBlank() && favouriteIds.contains(currentAnimeId))
                }
            }
        }
    }

    fun handleIntent(intent: AnimeDetailsIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is AnimeDetailsIntent.GetData -> getData(intent.animeId)
            AnimeDetailsIntent.RefreshData -> refreshAnimeDetail(currentAnimeId)
            AnimeDetailsIntent.ToggleFavourite -> toggleFavourite()
            AnimeDetailsIntent.OnNavigateBackClick -> _effect.emit(AnimeDetailsEffect.NavigateBack)
        }
    }

    private suspend fun getData(animeId: String) {
        currentAnimeId = animeId
        observeAnimeDetail(animeId)
        refreshAnimeDetail(animeId)
    }

    private fun observeAnimeDetail(animeId: String) {
        if (animeId.isBlank()) {
            _state.update { it.copy(uiState = AnimeDetailsUiState.Empty) }
            return
        }
        observeJob?.cancel()

        observeJob = viewModelScope.launch(coroutineExceptionHandler) {
            getUseCase(animeId)
                .catch { handleError(it) }
                .collect { animeDetail ->
                    if (animeDetail == null) {
                        _state.update { current ->
                            when (current.hasCompletedInitialSync) {
                                true -> current.copy(uiState = AnimeDetailsUiState.Empty)
                                false -> current.copy(uiState = AnimeDetailsUiState.Loading)
                            }
                        }
                        return@collect
                    }
                    _state.update { it.copy(uiState = AnimeDetailsUiState.Success(animeDetail)) }
                }
        }
    }

    private suspend fun refreshAnimeDetail(animeId: String) {
        if (animeId.isBlank()) {
            _state.update { it.copy(uiState = AnimeDetailsUiState.Empty) }
            return
        }

        val hasContent = _state.value.uiState is AnimeDetailsUiState.Success
        when (hasContent) {
            true -> _state.update { it.copy(isRefreshing = true) }
            false -> _state.update { it.copy(uiState = AnimeDetailsUiState.Loading) }
        }

        try {
            refreshUseCase(animeId)
        } catch (throwable: Throwable) {
            handleError(throwable)
        } finally {
            _state.update { it.copy(isRefreshing = false, hasCompletedInitialSync = true) }
        }
    }

    private suspend fun toggleFavourite() {
        val animeDetail = (_state.value.uiState as? AnimeDetailsUiState.Success)?.animeDetail ?: return
        toggleFavouriteAnimeUseCase(animeDetail.toAnime())
    }

    private fun handleError(throwable: Throwable) = viewModelScope.launch {
        Log.e(TAG, "AnimeDetailsViewModel: handleError: ", throwable)
        if (_state.value.uiState !is AnimeDetailsUiState.Success) {
            _state.update { it.copy(uiState = AnimeDetailsUiState.Error(throwable.toUiMessageRes()), hasCompletedInitialSync = true) }
        }
    }

    private fun Throwable.toUiMessageRes(): Int = when (this) {
        is DomainError.Network -> commonR.string.error_network
        is DomainError.Server -> commonR.string.error_server
        is DomainError.Client -> commonR.string.error_client
        is DomainError.Unknown -> commonR.string.error_unknown
        is UnknownHostException, is ConnectException, is SocketTimeoutException -> commonR.string.error_network
        else -> commonR.string.error_unknown
    }
}