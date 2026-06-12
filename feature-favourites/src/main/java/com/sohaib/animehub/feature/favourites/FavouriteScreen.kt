package com.sohaib.animehub.feature.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.feature.favourites.intent.FavouriteIntent
import com.sohaib.animehub.feature.favourites.viewModel.FavouriteViewModel
import org.koin.androidx.compose.koinViewModel
import com.sohaib.animehub.core.common.R as commonR

const val FAVOURITE_ROUTE = "favourites"

@Composable
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavouriteViewModel = koinViewModel(),
    onNavigateToDetailPage: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.favouriteAnimeList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(commonR.string.no_favourites_found),
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = state.favouriteAnimeList,
                key = { it.id },
            ) { anime ->
                FavouriteAnimeItem(
                    anime = anime,
                    onCardClick = { onNavigateToDetailPage(anime.id) },
                    onFavouriteClick = { viewModel.handleIntent(FavouriteIntent.ToggleFavourite(anime)) },
                )
            }
        }
    }
}

@Composable
private fun FavouriteAnimeItem(
    anime: Anime,
    onCardClick: () -> Unit,
    onFavouriteClick: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = onCardClick,
    ) {
        Box {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            IconButton(
                onClick = onFavouriteClick,
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = stringResource(commonR.string.remove_from_favourites),
                    tint = Color.Red,
                )
            }
            Text(
                text = anime.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
            )
        }
    }
}
