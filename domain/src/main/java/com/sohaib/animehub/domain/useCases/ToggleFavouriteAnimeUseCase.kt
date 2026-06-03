package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.repositories.PreferencesRepository

class ToggleFavouriteAnimeUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(animeId: String) {
        if (animeId.isBlank()) return
        preferencesRepository.toggleFavouriteAnimeId(animeId = animeId)
    }
}