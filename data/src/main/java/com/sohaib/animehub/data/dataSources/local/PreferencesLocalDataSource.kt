package com.sohaib.animehub.data.dataSources.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class PreferencesLocalDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val Context.dataStore by preferencesDataStore(name = "app_preferences")

    fun observeThemeModeRaw(): Flow<String> = context.dataStore.data.map { preferences -> preferences[THEME_MODE_KEY] ?: "SYSTEM" }

    fun observeFavouriteAnimeIds(): Flow<Set<String>> = context.dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences -> preferences[FAVOURITE_IDS_KEY] ?: emptySet() }

    suspend fun setThemeModeRaw(themeModeRaw: String) = withContext(ioDispatcher) {
        context.dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = themeModeRaw }
    }

    suspend fun toggleFavouriteAnimeId(animeId: String) = withContext(ioDispatcher) {
        context.dataStore.edit { preferences ->
            val existing = preferences[FAVOURITE_IDS_KEY] ?: emptySet()
            val updated = existing.toMutableSet().apply {
                if (contains(animeId)) remove(animeId) else add(animeId)
            }
            preferences[FAVOURITE_IDS_KEY] = updated
        }
    }

    private companion object {
        val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val FAVOURITE_IDS_KEY: Preferences.Key<Set<String>> = stringSetPreferencesKey("favourite_anime_ids")
    }
}