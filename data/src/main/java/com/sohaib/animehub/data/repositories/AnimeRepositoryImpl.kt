package com.sohaib.animehub.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sohaib.animehub.data.dataSources.local.AnimeLocalDataSource
import com.sohaib.animehub.data.dataSources.remote.AnimeRemoteDataSource
import com.sohaib.animehub.data.mapper.toDomain
import com.sohaib.animehub.data.mapper.toEntity
import com.sohaib.animehub.data.paging.AnimePagingConfig
import com.sohaib.animehub.data.paging.AnimeRemoteMediator
import com.sohaib.animehub.domain.errors.DomainError
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.models.AnimeDetail
import com.sohaib.animehub.domain.repositories.AnimeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class AnimeRepositoryImpl(
    private val localDataSource: AnimeLocalDataSource,
    private val remoteDataSource: AnimeRemoteDataSource,
    private val ioDispatchers: CoroutineDispatcher,
) : AnimeRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getAnimeListPaging(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(
                pageSize = AnimePagingConfig.PAGE_SIZE,
                initialLoadSize = AnimePagingConfig.PAGE_SIZE * 1,
                prefetchDistance = AnimePagingConfig.PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            remoteMediator = AnimeRemoteMediator(
                localDataSource = localDataSource,
                remoteDataSource = remoteDataSource,
            ),
            pagingSourceFactory = { localDataSource.getAnimePagingSource() },
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun observeAnimeByIds(animeIds: List<String>): Flow<List<Anime>> =
        localDataSource.observeAnimeByIds(animeIds = animeIds)
            .map { entities ->
                entities.toDomain()
                    .sortedBy { anime -> animeIds.indexOf(anime.id).takeIf { it >= 0 } ?: Int.MAX_VALUE }
            }

    override fun getAnimeDetails(animeId: String): Flow<AnimeDetail?> = localDataSource.getAnimeDetails(animeId = animeId).map { it.toDomain() }

    override suspend fun refreshAnimeDetailById(animeId: String): Unit = withContext(ioDispatchers) {
        try {
            val detail = remoteDataSource.getAnimeDetails(animeId = animeId)
            localDataSource.upsertDetail(detail.toEntity())
        } catch (throwable: Throwable) {
            throw throwable.toDomainError()
        }
    }

    private fun Throwable.toDomainError(): DomainError = when (this) {
        is DomainError -> this
        is IOException -> DomainError.Network(this)
        else -> {
            val message = message.orEmpty()
            when {
                message.contains("HTTP 5") -> DomainError.Server(code = 500, original = this)
                message.contains("HTTP 4") -> DomainError.Client(code = 400, original = this)
                else -> DomainError.Unknown(this)
            }
        }
    }
}