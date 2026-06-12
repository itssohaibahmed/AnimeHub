package com.sohaib.animehub.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.sohaib.animehub.domain.models.Anime
import com.sohaib.animehub.feature.home.effect.HomeEffect
import com.sohaib.animehub.feature.home.intent.HomeIntent
import com.sohaib.animehub.feature.home.state.HomeListUiState
import com.sohaib.animehub.feature.home.state.toHomeListUiState
import com.sohaib.animehub.feature.home.viewModel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import com.sohaib.animehub.core.common.R as commonR

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToDetailPage: (String) -> Unit,
) {
    val pagingItems = viewModel.animePagingFlow.collectAsLazyPagingItems()
    val favouriteAnimeIds by viewModel.favouriteAnimeIds.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.RefreshAnimeList -> pagingItems.refresh()
                is HomeEffect.NavigateToDetailPage -> onNavigateToDetailPage(effect.animeId)
            }
        }
    }

    HomeScreenContent(
        modifier = modifier,
        pagingItems = pagingItems,
        onCardClick = { viewModel.handleIntent(HomeIntent.OnItemClick(it)) },
        onFavouriteClick = { viewModel.handleIntent(HomeIntent.ToggleFavourite(it)) },
        favouriteAnimeIds = favouriteAnimeIds,
        onRefresh = { viewModel.handleIntent(HomeIntent.Refresh) },
        onRetryClicked = { viewModel.handleIntent(HomeIntent.Refresh) },
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<Anime>,
    onCardClick: (String) -> Unit,
    onFavouriteClick: (String) -> Unit,
    favouriteAnimeIds: Set<String>,
    onRefresh: () -> Unit,
    onRetryClicked: () -> Unit,
) {
    val uiState = pagingItems.toHomeListUiState()

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            HomeListUiState.FirstTimeLoading -> HomeFirstTimeLoadingContent()
            HomeListUiState.Empty -> HomeEmptyContent()
            is HomeListUiState.Error -> HomeErrorContent(messageResId = uiState.messageResId, onRetryClicked = onRetryClicked)
            is HomeListUiState.Success -> HomeSuccessContent(
                pagingItems = pagingItems,
                isRefreshing = uiState.isRefreshing,
                isLoadingMore = uiState.isLoadingMore,
                onCardClick = onCardClick,
                onFavouriteClick = onFavouriteClick,
                favouriteAnimeIds = favouriteAnimeIds,
                onRefresh = onRefresh,
            )
        }

        if (uiState is HomeListUiState.Success && uiState.isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun HomeFirstTimeLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeEmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(commonR.string.no_data_found), textAlign = TextAlign.Center)
    }
}

@Composable
private fun HomeErrorContent(
    messageResId: Int,
    onRetryClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(messageResId), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetryClicked) {
            Text(text = stringResource(commonR.string.retry))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSuccessContent(
    pagingItems: LazyPagingItems<Anime>,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    onCardClick: (String) -> Unit,
    onFavouriteClick: (String) -> Unit,
    favouriteAnimeIds: Set<String>,
    onRefresh: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val loadMoreBarHeight = 56.dp
        val gridHeight = if (isLoadingMore) maxHeight - loadMoreBarHeight else maxHeight

        Column(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight),
            ) {
                HomeAnimeGrid(
                    pagingItems = pagingItems,
                    onCardClick = onCardClick,
                    onFavouriteClick = onFavouriteClick,
                    favouriteAnimeIds = favouriteAnimeIds,
                )
            }

            if (isLoadingMore) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(loadMoreBarHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun HomeAnimeGrid(
    pagingItems: LazyPagingItems<Anime>,
    onCardClick: (String) -> Unit,
    onFavouriteClick: (String) -> Unit,
    favouriteAnimeIds: Set<String>,
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id },
        ) { index ->
            val anime = pagingItems[index]
            if (anime == null) {
                // Keep grid slot so order matches DB; do not skip with return@items.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
            } else {
                AnimeItem(
                    anime = anime,
                    onCardClick = onCardClick,
                    isFavourite = favouriteAnimeIds.contains(anime.id),
                    onFavouriteClick = onFavouriteClick,
                )
            }
        }
    }
}

@Composable
fun AnimeItem(
    modifier: Modifier = Modifier,
    anime: Anime,
    isFavourite: Boolean,
    onCardClick: (String) -> Unit,
    onFavouriteClick: (String) -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = { onCardClick(anime.id) },
    ) {
        Box {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            IconButton(
                onClick = { onFavouriteClick(anime.id) },
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Icon(
                    imageVector = if (isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavourite) {
                        stringResource(commonR.string.remove_from_favourites)
                    } else {
                        stringResource(commonR.string.add_to_favourites)
                    },
                    tint = if (isFavourite) Color.Red else Color.White,
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPrev() {
    AnimeItem(
        anime = Anime(id = "1", title = "Naruto", imageUrl = ""),
        onCardClick = {},
        isFavourite = false,
        onFavouriteClick = {},
    )
}