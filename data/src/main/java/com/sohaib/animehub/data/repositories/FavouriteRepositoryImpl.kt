package com.sohaib.animehub.data.repositories

import com.sohaib.animehub.data.dataSources.local.FavouriteLocalDataSource
import com.sohaib.animehub.data.mapper.toFavouriteDomain
import com.sohaib.animehub.data.mapper.toFavouriteEntity
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.repositories.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteRepositoryImpl(
    private val localDataSource: FavouriteLocalDataSource,
) : FavouriteRepository {

    override fun observeFavourites(): Flow<List<Anime>> =
        localDataSource.observeFavourites().map { entities -> entities.toFavouriteDomain() }

    override fun observeFavouriteIds(): Flow<Set<String>> =
        localDataSource.observeFavouriteIds().map { ids -> ids.toSet() }

    override suspend fun toggleFavourite(anime: Anime) {
        localDataSource.toggleFavourite(anime.toFavouriteEntity())
    }
}