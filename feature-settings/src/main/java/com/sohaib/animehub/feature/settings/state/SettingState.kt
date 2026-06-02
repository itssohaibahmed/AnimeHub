package com.sohaib.animehub.feature.settings.state

import com.sohaib.animehub.domain.enums.ThemeMode

data class SettingState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)