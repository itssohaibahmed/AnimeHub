package com.sohaib.animehub.feature.home.intent

import com.sohaib.animehub.domain.models.Anime

sealed class HomeIntent {
    data class OnItemClick(val animeId: String) : HomeIntent()
    data class ToggleFavourite(val anime: Anime) : HomeIntent()
    data object Refresh : HomeIntent()
}
