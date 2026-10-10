package com.strimup.feature.streamerprofile.presentation.streamerprofile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.mapper.getIconRes
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.browser.rememberExternalLinkOpener
import com.strimup.core.ui.component.button.SocialIconButton
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.component.spacer.VerticalSpacer
import com.strimup.core.ui.component.streamer.StreamerHero
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.streamer.displayName
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.schedule.presentation.schedulesection.ScheduleSection
import com.strimup.feature.streamerprofile.R
import com.strimup.feature.streamervideos.presentation.videossection.VideosSection
import com.strimup.core.ui.R as CoreUiR

@Composable
fun StreamerProfileScreen(
    modifier: Modifier = Modifier,
    onEditProfileNav: () -> Unit,
    onAccountNav: () -> Unit,
    onScheduleExportNav: (username: String) -> Unit,
    viewModel: StreamerProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val openExternalLink = rememberExternalLinkOpener(snackBarHostState)

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }
            }
        }
    }

    StreamerProfileScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onEditProfileNav = onEditProfileNav,
        onSocialClick = openExternalLink::invoke,
        onRetryClick = viewModel::refresh,
        onAccountClick = onAccountNav,
        scheduleContent = { streamer ->
            ScheduleSection(
                streamerId = streamer.id,
                isEditable = true,
                snackBarHostState = snackBarHostState,
                onExportClick = { onScheduleExportNav(streamer.userName) },
            )
        },
        videosContent = { VideosSection(snackBarHostState = snackBarHostState) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerProfileScreen(
    state: ProfileUiState,
    snackBarHostState: SnackbarHostState,
    onEditProfileNav: () -> Unit,
    onSocialClick: (String?) -> Unit,
    onRetryClick: () -> Unit,
    onAccountClick: () -> Unit,
    scheduleContent: @Composable (streamer: Streamer) -> Unit,
    videosContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleText = when (state) {
        is ProfileUiState.Success -> state.streamer.userName
        else -> ""
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = onAccountClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(CoreUiR.string.account_title),
                        )
                    }
                },
            )
        },
    ) { padding ->
        StreamerProfileContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            onEditProfileNav = onEditProfileNav,
            onSocialClick = onSocialClick,
            onRetryClick = onRetryClick,
            scheduleContent = scheduleContent,
            videosContent = videosContent,
            state = state,
        )
    }
}

@Composable
private fun StreamerProfileContent(
    state: ProfileUiState,
    onEditProfileNav: () -> Unit,
    onSocialClick: (String?) -> Unit,
    onRetryClick: () -> Unit,
    scheduleContent: @Composable (streamer: Streamer) -> Unit,
    videosContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ProfileUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is ProfileUiState.Error -> {
            ErrorState(
                messageRes = state.errorMessageRes,
                onRetryClick = onRetryClick,
                modifier = modifier.fillMaxSize(),
            )
        }

        is ProfileUiState.Success -> {
            StreamerProfileSuccessContent(
                streamer = state.streamer,
                onEditProfileNav = onEditProfileNav,
                onSocialClick = onSocialClick,
                scheduleContent = scheduleContent,
                videosContent = videosContent,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun StreamerProfileSuccessContent(
    streamer: Streamer,
    onEditProfileNav: () -> Unit,
    onSocialClick: (String?) -> Unit,
    scheduleContent: @Composable (streamer: Streamer) -> Unit,
    videosContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        StreamerHero(
            modifier = Modifier.fillMaxWidth(),
            isLive = streamer.isLive,
            imageUrl = streamer.imageUrl ?: "",
            pseudo = streamer.userName,
            tags = streamer.tags?.map { it.name },
            dailyStatus = streamer.dailyStatus ?: "",
            followersCount = streamer.followersCount,
            isFavorite = false,
            onFavoriteClick = {},
            isProfile = true
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEditProfileNav,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.profile_edit_title),
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                VerticalSpacer(24.dp)

                if (streamer.socials.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        streamer.socials.forEach { social ->
                            SocialIconButton(
                                iconRes = social.getIconRes(),
                                contentDescription = stringResource(
                                    CoreUiR.string.streamer_open_social,
                                    social.type.displayName(),
                                ),
                                onClick = { onSocialClick(social.url) }
                            )
                        }
                    }
                    VerticalSpacer(24.dp)
                }

                Text(
                    text = stringResource(CoreUiR.string.profile_about),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                )

                VerticalSpacer(8.dp)

                streamer.bio?.let { bioText ->
                    Text(
                        text = bioText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .animateContentSize()
                            .clickable { isExpanded = !isExpanded }
                    )

                    if (bioText.length > 120) {
                        Text(
                            text = stringResource(
                                if (isExpanded) CoreUiR.string.profile_read_less else CoreUiR.string.profile_read_more
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { isExpanded = !isExpanded }
                        )
                    }
                }

                VerticalSpacer(24.dp)

                scheduleContent(streamer)

                VerticalSpacer(24.dp)

                videosContent()

                VerticalSpacer(24.dp)
            }
        }
    }
}

@Preview
@Composable
private fun StreamerProfileScreenPreview() {
    StrimupTheme {
        StreamerProfileScreen(
            snackBarHostState = remember { SnackbarHostState() },
            state = ProfileUiState.Success(
                streamer = Streamer(
                    id = "1",
                    isLive = true,
                    bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting. Je partage également toutes mes activités (Création graphique, montage vidéo)...",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezie",
                    tags = listOf(
                        TagEntity(name = "Gaming", category = "dolk", id = 3),
                        TagEntity(name = "Dev", category = "dolk", id = 34)
                    ),
                    dailyStatus = "Hello la compagnie !",
                    videos = emptyList(),
                    socials = listOf(
                        Social(
                            url = "",
                            type = Social.Type.Twitch
                        ),
                        Social(
                            url = "",
                            type = Social.Type.Youtube
                        )
                    ),
                    followersCount = 10,
                    averageViewers = "4",
                    languages = emptyList(),
                    personality = "",
                    personalitySecondary = "",
                    streamFrequency = "",
                )
            ),
            onEditProfileNav = {},
            onSocialClick = {},
            onRetryClick = {},
            onAccountClick = {},
            scheduleContent = {},
            videosContent = {},
        )
    }
}