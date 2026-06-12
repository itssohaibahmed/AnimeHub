package com.sohaib.animehub.data.dataSources.local

import com.sohaib.animehub.core.database.daos.FavouriteAnimeDao
import com.sohaib.animehub.core.database.entities.FavouriteAnimeEntity
import kotlinx.coroutines.flow.Flow

class FavouriteLocalDataSource(
    private val favouriteAnimeDao: FavouriteAnimeDao,
) {
    fun observeFavourites(): Flow<List<FavouriteAnimeEntity>> = favouriteAnimeDao.observeFavourites()

    fun observeFavouriteIds(): Flow<List<String>> = favouriteAnimeDao.observeFavouriteIds()

    suspend fun toggleFavourite(entity: FavouriteAnimeEntity) {
        if (favouriteAnimeDao.countById(entity.id) > 0) {
            favouriteAnimeDao.deleteById(entity.id)
        } else {
            favouriteAnimeDao.upsert(entity)
        }
    }
}