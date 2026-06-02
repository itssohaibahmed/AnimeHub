package com.sohaib.animehub.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sohaib.animehub.core.common.extensions.shareText
import com.sohaib.animehub.domain.enums.ThemeMode
import com.sohaib.animehub.feature.settings.effect.SettingEffect
import com.sohaib.animehub.feature.settings.intent.SettingIntent
import com.sohaib.animehub.feature.settings.state.SettingState
import com.sohaib.animehub.feature.settings.viewModel.SettingViewModel
import org.koin.androidx.compose.koinViewModel
import com.sohaib.animehub.core.common.R as commonR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingEffect.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(effect.message)
                }
                is SettingEffect.OpenUrl -> uriHandler.openUri(effect.url)
                is SettingEffect.ShareText -> context.shareText(effect.text)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        SettingScreenContent(
            modifier = Modifier.padding(padding),
            state = state.value,
            onIntent = viewModel::handleIntent,
        )
    }
}

@Composable
private fun SettingScreenContent(
    modifier: Modifier = Modifier,
    state: SettingState,
    onIntent: (SettingIntent) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionHeader(stringResource(commonR.string.settings_section_appearance))
        ThemeModeSelector(
            selected = state.themeMode,
            onSelect = { onIntent(SettingIntent.SelectThemeMode(it)) },
        )

        SectionHeader(stringResource(commonR.string.settings_section_support))
        SettingActionItem(
            title = stringResource(commonR.string.settings_rate_app),
            subtitle = stringResource(commonR.string.settings_rate_app_subtitle),
            onClick = { onIntent(SettingIntent.OpenRateApp) },
        )
        SettingActionItem(
            title = stringResource(commonR.string.settings_feedback),
            subtitle = stringResource(commonR.string.settings_feedback_subtitle),
            onClick = { onIntent(SettingIntent.OpenFeedback) },
        )
        SettingActionItem(
            title = stringResource(commonR.string.settings_share_app),
            subtitle = stringResource(commonR.string.settings_share_app_subtitle),
            onClick = { onIntent(SettingIntent.ShareApp) },
        )

        SectionHeader(stringResource(commonR.string.settings_section_about))
        AboutDeveloperCard(
            onGithubClick = { onIntent(SettingIntent.OpenGithub) },
            onLinkedinClick = { onIntent(SettingIntent.OpenLinkedin) },
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SettingActionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = title, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = stringResource(commonR.string.settings_action_open), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AboutDeveloperCard(
    onGithubClick: () -> Unit,
    onLinkedinClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(commonR.string.settings_about_title),
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = stringResource(commonR.string.settings_about_body),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(commonR.string.settings_about_github),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable(onClick = onGithubClick),
            )
            Text(
                text = stringResource(commonR.string.settings_about_linkedin),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable(onClick = onLinkedinClick),
            )
        }
    }
}

@Composable
private fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = stringResource(commonR.string.settings_theme_mode),
                fontWeight = FontWeight.Medium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = selected == mode,
                        onClick = { onSelect(mode) },
                        label = {
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> stringResource(commonR.string.settings_theme_system)
                                    ThemeMode.LIGHT -> stringResource(commonR.string.settings_theme_light)
                                    ThemeMode.DARK -> stringResource(commonR.string.settings_theme_dark)
                                }
                            )
                        },
                    )
                }
            }
        }
    }
}