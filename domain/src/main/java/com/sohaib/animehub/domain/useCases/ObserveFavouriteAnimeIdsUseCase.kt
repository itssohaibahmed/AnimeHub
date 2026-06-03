package com.sohaib.animehub.domain.useCases

import com.sohaib.animehub.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavouriteAnimeIdsUseCase(
    private val preferencesRepository: PreferencesRepository,
) {
    operator fun invoke(): Flow<Set<String>> = preferencesRepository.favouriteAnimeIds
}