package com.sohaib.animehub.feature.anime.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sohaib.animehub.domain.models.AnimeDetail
import com.sohaib.animehub.feature.anime.details.effect.AnimeDetailsEffect
import com.sohaib.animehub.feature.anime.details.intent.AnimeDetailsIntent
import com.sohaib.animehub.feature.anime.details.state.AnimeDetailsState
import com.sohaib.animehub.feature.anime.details.state.AnimeDetailsUiState
import com.sohaib.animehub.feature.anime.details.viewModel.AnimeDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import com.sohaib.animehub.core.common.R as commonR

const val ANIME_DETAILS_ROUTE = "anime_details/{animeId}"
fun createRouteAnimeDetails(animeId: String) = "anime_details/$animeId"

@Composable
fun AnimeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnimeDetailsViewModel = koinViewModel(),
    animeId: String? = null,
    onNavigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(animeId) {
        viewModel.handleIntent(AnimeDetailsIntent.GetData(animeId.orEmpty()))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AnimeDetailsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AnimeDetailsContent(
        modifier = modifier,
        state = state.value,
        onNavigateBack = { viewModel.handleIntent(AnimeDetailsIntent.OnNavigateBackClick) },
        onRefresh = { viewModel.handleIntent(AnimeDetailsIntent.RefreshData) },
        onToggleFavourite = { viewModel.handleIntent(AnimeDetailsIntent.ToggleFavourite) },
        onRetry = { viewModel.handleIntent(AnimeDetailsIntent.RefreshData) },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AnimeDetailsContent(
    modifier: Modifier = Modifier,
    state: AnimeDetailsState,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onToggleFavourite: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val uiState = state.uiState) {
                        is AnimeDetailsUiState.Success -> uiState.animeDetail.title
                        else -> stringResource(R.string.anime_details)
                    }
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = stringResource(commonR.string.navigate_back),
                        )
                    }
                },
                actions = {
                    if (state.uiState is AnimeDetailsUiState.Success) {
                        IconButton(onClick = onToggleFavourite) {
                            Icon(
                                imageVector = if (state.isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = if (state.isFavourite) {
                                    stringResource(commonR.string.remove_from_favourites)
                                } else {
                                    stringResource(commonR.string.add_to_favourites)
                                },
                                tint = if (state.isFavourite) Color.Red else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (val uiState = state.uiState) {
                AnimeDetailsUiState.Loading -> CircularProgressIndicator()
                is AnimeDetailsUiState.Error -> AnimeDetailsErrorContent(
                    messageResId = uiState.messageResId,
                    onRetry = onRetry,
                )
                AnimeDetailsUiState.Empty -> Text(text = stringResource(commonR.string.no_data_found))
                is AnimeDetailsUiState.Success -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AnimeDetailsSuccessState(animeDetail = uiState.animeDetail)
                }
            }

            if (state.isRefreshing && state.uiState !is AnimeDetailsUiState.Success) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
private fun AnimeDetailsErrorContent(
    messageResId: Int,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = stringResource(messageResId), textAlign = TextAlign.Center)
        Button(onClick = onRetry) {
            Text(text = stringResource(commonR.string.retry))
        }
    }
}

@Composable
private fun AnimeDetailsSuccessState(
    animeDetail: AnimeDetail,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = animeDetail.coverImageLargeUrl.ifBlank { animeDetail.posterImageLargeUrl },
            contentDescription = animeDetail.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )
        Text(
            text = animeDetail.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (animeDetail.slug.isNotBlank()) {
            Text(
                text = stringResource(commonR.string.anime_slug, animeDetail.slug),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        Text(
            text = stringResource(commonR.string.episodes_count, animeDetail.episodeCount),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (animeDetail.youtubeVideoId.isNotBlank()) {
            TextButton(
                onClick = { uriHandler.openUri("https://www.youtube.com/watch?v=${animeDetail.youtubeVideoId}") },
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Text(text = stringResource(commonR.string.anime_watch_trailer))
            }
        }
        if (animeDetail.updatedAt.isNotBlank()) {
            Text(
                text = stringResource(commonR.string.anime_last_updated, animeDetail.updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (animeDetail.description.isNotBlank()) {
            Text(
                text = stringResource(commonR.string.anime_synopsis),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Text(
                text = animeDetail.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp),
            )
        } else {
            Box(modifier = Modifier.padding(bottom = 40.dp))
        }
    }
}