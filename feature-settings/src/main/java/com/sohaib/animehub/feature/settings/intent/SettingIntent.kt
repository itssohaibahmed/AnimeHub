package com.sohaib.animehub.feature.settings.intent

import com.sohaib.animehub.domain.enums.ThemeMode

sealed class SettingIntent {
    data class SelectThemeMode(val mode: ThemeMode) : SettingIntent()
    data object ObserveThemePreference : SettingIntent()
    data object OpenRateApp : SettingIntent()
    data object OpenFeedback : SettingIntent()
    data object ShareApp : SettingIntent()
    data object OpenGithub : SettingIntent()
    data object OpenLinkedin : SettingIntent()
}