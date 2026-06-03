package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.repositories.AnimeRepository
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GetFavouriteAnimeListUseCase(
    private val animeRepository: AnimeRepository,
    private val preferencesRepository: PreferencesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Anime>> = preferencesRepository.favouriteAnimeIds.flatMapLatest { ids ->
        if (ids.isEmpty()) flowOf(emptyList())
        else animeRepository.observeAnimeByIds(ids.toList())
    }
}