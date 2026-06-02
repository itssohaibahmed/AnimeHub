package com.sohaib.animehub.domain.repositories

import com.sohaib.animehub.domain.enums.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
}