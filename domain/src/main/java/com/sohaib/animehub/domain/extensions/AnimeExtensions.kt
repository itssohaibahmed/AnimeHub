package com.sohaib.animehub.domain.extensions

import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.domain.models.AnimeDetail

fun AnimeDetail.toAnime(): Anime = Anime(
    id = id,
    title = title,
    imageUrl = posterImageLargeUrl.ifBlank { coverImageLargeUrl },
)