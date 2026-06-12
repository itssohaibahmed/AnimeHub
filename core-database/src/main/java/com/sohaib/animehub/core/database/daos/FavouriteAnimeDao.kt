package com.sohaib.animehub.core.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.sohaib.animehub.core.database.entities.FavouriteAnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteAnimeDao {

    @Query("SELECT * FROM table_favourite_anime ORDER BY savedAt DESC")
    fun observeFavourites(): Flow<List<FavouriteAnimeEntity>>

    @Query("SELECT id FROM table_favourite_anime")
    fun observeFavouriteIds(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM table_favourite_anime WHERE id = :animeId")
    suspend fun countById(animeId: String): Int

    @Upsert
    suspend fun upsert(favourite: FavouriteAnimeEntity)

    @Query("DELETE FROM table_favourite_anime WHERE id = :animeId")
    suspend fun deleteById(animeId: String)
}