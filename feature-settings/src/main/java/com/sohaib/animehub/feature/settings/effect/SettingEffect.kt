package com.sohaib.animehub.feature.settings.effect

sealed class SettingEffect {
    data class ShowMessage(val message: String) : SettingEffect()
    data class OpenUrl(val url: String) : SettingEffect()
    data class ShareText(val text: String) : SettingEffect()
}