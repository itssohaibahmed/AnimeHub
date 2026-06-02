package com.sohaib.animehub.domain.repositories

import com.sohaib.animehub.domain.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val themeMode: Flow<ThemeMode>
    val favouriteAnimeIds: Flow<Set<String>>
    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun toggleFavouriteAnimeId(animeId: String)
}