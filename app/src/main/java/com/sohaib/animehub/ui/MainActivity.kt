package com.sohaib.animehub.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.sohaib.animehub.core.design.AnimeHubTheme
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import com.sohaib.animehub.domain.enums.ThemeMode
import com.sohaib.animehub.navigation.NavGraph
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    private val preferencesRepository: PreferencesRepository by lazy {
        GlobalContext.get().get<PreferencesRepository>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by preferencesRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val isDarkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            AnimeHubTheme(darkTheme = isDarkTheme) {
                NavGraph()
            }
        }
    }
}