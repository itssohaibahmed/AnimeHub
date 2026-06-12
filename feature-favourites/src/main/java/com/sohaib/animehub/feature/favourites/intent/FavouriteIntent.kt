package com.sohaib.animehub.feature.favourites.intent

import com.sohaib.animehub.domain.models.Anime

sealed class FavouriteIntent {
    data object GetFavouriteList : FavouriteIntent()
    data class ToggleFavourite(val anime: Anime) : FavouriteIntent()
}
