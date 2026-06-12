package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.repositories.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavouriteAnimeListUseCase(
    private val favouriteRepository: FavouriteRepository,
) {
    operator fun invoke(): Flow<List<Anime>> = favouriteRepository.observeFavourites()
}