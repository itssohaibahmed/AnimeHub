package com.sohaib.animehub.data.dataSources.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesLocalDataSource(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "app_preferences")

    fun observeThemeModeRaw(): Flow<String> = context.dataStore.data.map { preferences -> preferences[THEME_MODE_KEY] ?: "SYSTEM" }

    suspend fun setThemeModeRaw(themeModeRaw: String) = context.dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = themeModeRaw }

    private companion object {
        val THEME_MODE_KEY: Preferences.Key<String> = stringPreferencesKey("theme_mode")
    }
}