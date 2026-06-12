package com.sohaib.animehub.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_favourite_anime")
data class FavouriteAnimeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val posterImageLargeUrl: String,
    val savedAt: Long,
)