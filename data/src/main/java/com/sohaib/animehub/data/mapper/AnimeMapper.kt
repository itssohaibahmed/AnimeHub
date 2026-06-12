package com.sohaib.animehub.data.mapper

import com.sohaib.animehub.core.database.entities.AnimeEntity
import com.sohaib.animehub.core.database.entities.FavouriteAnimeEntity
import com.sohaib.animehub.core.network.models.AnimeDTO
import com.sohaib.animehub.core.network.models.AnimeDetailDTO
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.models.AnimeDetail

/* --------------------------------------------- Room Entities --------------------------------------------- */

fun AnimeEntity.toDomain(): Anime = Anime(
    id = id,
    title = title,
    imageUrl = posterImageLargeUrl,
)

fun List<AnimeEntity>.toDomain(): List<Anime> = map { it.toDomain() }

fun FavouriteAnimeEntity.toDomain(): Anime = Anime(
    id = id,
    title = title,
    imageUrl = posterImageLargeUrl,
)

fun List<FavouriteAnimeEntity>.toFavouriteDomain(): List<Anime> = map { it.toDomain() }

fun Anime.toFavouriteEntity(): FavouriteAnimeEntity = FavouriteAnimeEntity(
    id = id,
    title = title,
    posterImageLargeUrl = imageUrl,
    savedAt = System.currentTimeMillis(),
)


fun AnimeEntity?.toDomain(): AnimeDetail? {
    this ?: return null
    return AnimeDetail(
        id = this.id,
        title = this.title,
        posterImageLargeUrl = this.posterImageLargeUrl,

        coverImageLargeUrl = this.coverImageLargeUrl.orEmpty(),
        description = this.description.orEmpty(),
        slug = this.slug.orEmpty(),
        episodeCount = this.episodeCount ?: 0,
        youtubeVideoId = this.youtubeVideoId.orEmpty(),
        createdAt = this.createdAt.orEmpty(),
        updatedAt = this.updatedAt.orEmpty()
    )
}

/* --------------------------------------------- DTO's (if needed) --------------------------------------------- */

fun AnimeDTO.toEntity(): List<AnimeEntity> {
    return data.map { item ->
        val attributes = item.attributes
        val titles = attributes?.titles
        AnimeEntity(
            id = item.id.orEmpty(),
            title = titles?.en
                ?: titles?.en_us
                ?: titles?.en_jp
                ?: titles?.ja_jp
                ?: attributes?.canonicalTitle
                    .orEmpty(),
            // Some responses return http image URLs; normalize to https for Android network security.
            posterImageLargeUrl = attributes?.posterImage?.small.orEmpty(),
        )
    }
}

fun AnimeDetailDTO.toEntity(): AnimeEntity {
    val attributes = data.attributes
    val titles = attributes?.titles
    return AnimeEntity(
        id = data.id.orEmpty(),
        title = titles?.en
            ?: titles?.en_us
            ?: titles?.en_jp
            ?: titles?.ja_jp
            ?: attributes?.canonicalTitle
                .orEmpty(),
        posterImageLargeUrl = attributes?.posterImage?.large.orEmpty(),

        coverImageLargeUrl = attributes?.coverImage?.large,
        description = attributes?.description,
        slug = attributes?.slug,
        episodeCount = attributes?.episodeCount,
        youtubeVideoId = attributes?.youtubeVideoId,
        createdAt = attributes?.createdAt,
        updatedAt = attributes?.updatedAt
    )
}