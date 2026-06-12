package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.repositories.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavouriteAnimeIdsUseCase(
    private val favouriteRepository: FavouriteRepository,
) {
    operator fun invoke(): Flow<Set<String>> = favouriteRepository.observeFavouriteIds()
}