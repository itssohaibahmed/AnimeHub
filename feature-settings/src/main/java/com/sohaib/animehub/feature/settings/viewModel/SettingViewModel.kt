package com.sohaib.animehub.feature.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohaib.animehub.core.common.constants.ConfigUtils.FEEDBACK_URL
import com.sohaib.animehub.domain.enums.ThemeMode
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import com.sohaib.animehub.feature.settings.effect.SettingEffect
import com.sohaib.animehub.feature.settings.intent.SettingIntent
import com.sohaib.animehub.feature.settings.state.SettingState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private companion object {
        const val GITHUB_URL = "https://github.com/itssohaibahmed"
        const val LINKEDIN_URL = "https://www.linkedin.com/in/itssohaibahmed"
    }

    private val _state = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingEffect>()
    val effect: SharedFlow<SettingEffect> = _effect.asSharedFlow()

    init {
        handleIntent(SettingIntent.ObserveThemePreference)
    }

    fun handleIntent(intent: SettingIntent) = viewModelScope.launch {
        when (intent) {
            SettingIntent.ObserveThemePreference -> observePersistedTheme()
            is SettingIntent.SelectThemeMode -> onThemeChanged(intent)
            SettingIntent.OpenRateApp -> _effect.emit(SettingEffect.OpenUrl(FEEDBACK_URL))
            SettingIntent.OpenFeedback -> _effect.emit(SettingEffect.OpenUrl(FEEDBACK_URL))
            SettingIntent.ShareApp -> _effect.emit(SettingEffect.ShareText(text = "Check out AnimeHub: discover anime with a clean and modern experience."))
            SettingIntent.OpenGithub -> _effect.emit(SettingEffect.OpenUrl(GITHUB_URL))
            SettingIntent.OpenLinkedin -> _effect.emit(SettingEffect.OpenUrl(LINKEDIN_URL))
        }
    }

    private suspend fun observePersistedTheme() {
        preferencesRepository.themeMode.collect { mode: ThemeMode ->
            _state.update { current -> current.copy(themeMode = mode) }
        }
    }

    private suspend fun onThemeChanged(intent: SettingIntent.SelectThemeMode) {
        preferencesRepository.setThemeMode(intent.mode)
        _effect.emit(SettingEffect.ShowMessage("Theme changed to ${intent.mode.name.lowercase()}"))
    }
}