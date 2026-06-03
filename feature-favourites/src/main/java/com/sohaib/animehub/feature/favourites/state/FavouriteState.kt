package com.sohaib.animehub.feature.favourites.state

import com.sohaib.animehub.domain.models.Anime

data class FavouriteState(
    val favouriteAnimeList: List<Anime> = emptyList(),
)