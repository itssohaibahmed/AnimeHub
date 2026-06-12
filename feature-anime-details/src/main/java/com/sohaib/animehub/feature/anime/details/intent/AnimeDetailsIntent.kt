package com.sohaib.animehub.feature.anime.details.intent

sealed class AnimeDetailsIntent {
    data class GetData(val animeId: String) : AnimeDetailsIntent()
    data object RefreshData : AnimeDetailsIntent()
    data object ToggleFavourite : AnimeDetailsIntent()
    data object OnNavigateBackClick : AnimeDetailsIntent()
}