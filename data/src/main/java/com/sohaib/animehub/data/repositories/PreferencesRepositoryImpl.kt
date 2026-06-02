package com.sohaib.animehub.data.repositories

import com.sohaib.animehub.data.dataSources.local.PreferencesLocalDataSource
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import com.sohaib.animehub.domain.enums.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepositoryImpl(private val localDataSource: PreferencesLocalDataSource) : PreferencesRepository {

    override val themeMode: Flow<ThemeMode> = localDataSource.observeThemeModeRaw().map { raw ->
        ThemeMode.entries.firstOrNull { it.name == raw } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        localDataSource.setThemeModeRaw(themeMode.name)
    }
}