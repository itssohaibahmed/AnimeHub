package com.sohaib.animehub.domain.repositories

import androidx.paging.PagingData
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.models.AnimeDetail
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getAnimeListPaging(): Flow<PagingData<Anime>>
    fun getAnimeDetails(animeId: String): Flow<AnimeDetail?>
    suspend fun refreshAnimeDetailById(animeId: String)
}