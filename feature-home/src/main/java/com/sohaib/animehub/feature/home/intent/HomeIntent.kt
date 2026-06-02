package com.sohaib.animehub.feature.home.intent

sealed class HomeIntent {
    data class OnItemClick(val animeId: String) : HomeIntent()
    data class ToggleFavourite(val animeId: String) : HomeIntent()
    data object Refresh : HomeIntent()
}
