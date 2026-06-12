package com.sohaib.animehub.domain.repositories

import com.sohaib.animehub.domain.models.Anime
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun observeFavourites(): Flow<List<Anime>>
    fun observeFavouriteIds(): Flow<Set<String>>
    suspend fun toggleFavourite(anime: Anime)
}