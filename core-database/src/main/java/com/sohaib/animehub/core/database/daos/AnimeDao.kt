package com.sohaib.animehub.core.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Upsert
import com.sohaib.animehub.core.database.entities.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    /* -------------------------------- Reading Data -------------------------------- */

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT id, title, posterImageLargeUrl FROM table_anime")
    fun getAnimePagingSource(): PagingSource<Int, AnimeEntity>

    @Query("SELECT * FROM table_anime WHERE id = :animeId")
    fun getAnimeById(animeId: String): Flow<AnimeEntity?>

    @Upsert
    suspend fun upsertAnime(anime: AnimeEntity)

    @Query("SELECT COUNT(*) FROM table_anime")
    suspend fun getAnimeCount(): Int

    @Query("SELECT * FROM table_anime WHERE id IN (:animeIds)")
    suspend fun getAnimeByIds(animeIds: List<String>): List<AnimeEntity>

    /* -------------------------------- Writing Data -------------------------------- */

    @Upsert
    suspend fun upsertAll(items: List<AnimeEntity>)

    @Query("DELETE FROM table_anime")
    suspend fun clearAll()
}