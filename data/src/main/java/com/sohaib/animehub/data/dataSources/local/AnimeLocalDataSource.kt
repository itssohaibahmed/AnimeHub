package com.sohaib.animehub.data.dataSources.local

import androidx.room.withTransaction
import com.sohaib.animehub.core.database.AppDatabase
import com.sohaib.animehub.core.database.entities.AnimeEntity
import com.sohaib.animehub.core.database.entities.AnimeRemoteKeysEntity
import kotlinx.coroutines.flow.Flow

class AnimeLocalDataSource(
    private val appDatabase: AppDatabase,
) {

    private val animeDao get() = appDatabase.animeDao()
    private val remoteKeysDao get() = appDatabase.animeRemoteKeysDao()

    /* ------------------------------------------ AnimeDao ------------------------------------------ */

    /** Get items page by page in chunks **/
    fun getAnimePagingSource() = animeDao.getAnimePagingSource()

    /** Get item detail **/
    fun getAnimeDetails(animeId: String): Flow<AnimeEntity?> = animeDao.getAnimeById(animeId = animeId)

    /** Update if exist else add detail of item **/
    suspend fun upsertDetail(anime: AnimeEntity) = animeDao.upsertAnime(anime)

    suspend fun getAnimeCount(): Int = animeDao.getAnimeCount()

    fun observeAnimeByIds(animeIds: List<String>): Flow<List<AnimeEntity>> = animeDao.observeAnimeByIds(animeIds = animeIds)

    /**
     * Applies one remote page atomically:
     * - optional cache clear on refresh
     * - list upsert
     * - remote key update
     *
     * Network I/O must happen before calling this.
     */
    suspend fun applyPagedRemoteResponse(
        isRefresh: Boolean,
        entities: List<AnimeEntity>,
        nextOffset: Int?,
    ) {
        appDatabase.withTransaction {
            if (isRefresh) {
                animeDao.clearAll()
                remoteKeysDao.clearAll()
            }

            if (entities.isNotEmpty()) {
                animeDao.upsertAll(entities)
            }

            remoteKeysDao.insertRemoteKeys(
                AnimeRemoteKeysEntity(nextOffset = nextOffset),
            )
        }
    }

    /* ------------------------------------------ RemoteKeysDao ------------------------------------------ */

    suspend fun getNextOffset(): Int? = remoteKeysDao.getRemoteKeys()?.nextOffset
}
