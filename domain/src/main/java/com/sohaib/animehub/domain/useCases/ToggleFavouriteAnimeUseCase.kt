package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.repositories.FavouriteRepository

class ToggleFavouriteAnimeUseCase(
    private val favouriteRepository: FavouriteRepository,
) {
    suspend operator fun invoke(anime: Anime) {
        if (anime.id.isBlank()) return
        favouriteRepository.toggleFavourite(anime)
    }
}