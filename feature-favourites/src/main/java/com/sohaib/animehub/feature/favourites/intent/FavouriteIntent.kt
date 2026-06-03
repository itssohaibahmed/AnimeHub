package com.sohaib.animehub.feature.favourites.intent

sealed class FavouriteIntent {
    data object GetFavouriteList : FavouriteIntent()
    data class ToggleFavourite(val animeId: String) : FavouriteIntent()
}