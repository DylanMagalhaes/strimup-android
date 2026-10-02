package com.strimup.feature.streamerdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.browser.rememberExternalLinkOpener
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.component.streamer.StreamerContent
import com.strimup.core.ui.component.streamer.StreamerHero
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.report.presentation.ReportStreamerSheet
import com.strimup.feature.report.presentation.ReportStreamerUiEvent
import com.strimup.feature.report.presentation.ReportStreamerViewModel

@Composable
fun StreamerDetailScreen(
    streamerId: String,
    onNavUp: () -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
    isLoggedIn: Boolean,
    isOwnProfile: Boolean,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamerDetailViewModel = hiltViewModel(),
    reportViewModel: ReportStreamerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reportState by reportViewModel.state.collectAsStateWithLifecycle()
    var isReportSheetVisible by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val openExternalLink = rememberExternalLinkOpener(snackBarHostState)

    LaunchedEffect(streamerId) {
        viewModel.loadStreamer(streamerId)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is StreamerDetailUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        reportViewModel.events.collect { event ->
            when (event) {
                is ReportStreamerUiEvent.Reported -> {
                    isReportSheetVisible = false
                    snackBarHostState.showSnackbar(resources.getString(event.messageRes))
                }
            }
        }
    }

    StreamerDetailScreen(
        modifier = modifier,
        state = state,
        snackBarHostState = snackBarHostState,
        onNavUp = onNavUp,
        onSocialClick = openExternalLink::invoke,
        onVideoClick = onVideoClick,
        onFavoriteClick = { viewModel.onFavoriteClick() },
        onRetryClick = { viewModel.loadStreamer(streamerId) },
        isReportAvailable = !isOwnProfile,
        onReportClick = {
            if (isLoggedIn) isReportSheetVisible = true else onLoginRequired()
        },
    )

    val reportedStreamer = (state as? StreamerDetailUiState.Success)?.streamer
    if (isReportSheetVisible && reportedStreamer != null) {
        ReportStreamerSheet(
            streamerName = reportedStreamer.userName,
            state = reportState,
            onReasonSelected = reportViewModel::onReasonSelected,
            onDetailsChange = reportViewModel::onDetailsChange,
            onSubmitClick = { reportViewModel.onSubmitClick(reportedStreamer.id) },
            onDismiss = {
                isReportSheetVisible = false
                reportViewModel.onDismiss()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerDetailScreen(
    state: StreamerDetailUiState,
    snackBarHostState: SnackbarHostState,
    onNavUp: () -> Unit,
    onSocialClick: (String?) -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetryClick: () -> Unit,
    isReportAvailable: Boolean,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state is StreamerDetailUiState.Success) state.streamer.userName else "",
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    if (state is StreamerDetailUiState.Success && isReportAvailable) {
                        StreamerDetailMenu(onReportClick = onReportClick)
                    }
                },
            )
        },
    ) { padding ->
        StreamerDetailContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
            onSocialClick = onSocialClick,
            onVideoClick = onVideoClick,
            onFavoriteClick = onFavoriteClick,
            onRetryClick = onRetryClick,
        )
    }
}

@Composable
private fun StreamerDetailMenu(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.action_more_options),
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.report_action)) },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Flag, contentDescription = null) },
                onClick = {
                    isExpanded = false
                    onReportClick()
                },
            )
        }
    }
}

@Composable
private fun StreamerDetailContent(
    state: StreamerDetailUiState,
    onSocialClick: (String?) -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    when (state) {
        is StreamerDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        is StreamerDetailUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                StreamerHero(
                    modifier = Modifier.fillMaxWidth(),
                    isLive = state.streamer.isLive,
                    imageUrl = state.streamer.imageUrl ?: "",
                    pseudo = state.streamer.userName,
                    tags = state.streamer.tags?.map { it.name },
                    dailyStatus = state.streamer.dailyStatus ?: "",
                    followersCount = state.streamer.followersCount,
                    onFavoriteClick = onFavoriteClick,
                    isFavorite = state.isFavorite,
                    isProfile = false

                )

                StreamerContent(
                    modifier = Modifier.fillMaxSize(),
                    description = state.streamer.bio ?: "",
                    socials = state.streamer.socials,
                    onSocialClick = onSocialClick,
                    videos = state.streamer.videos,
                    onVideoClick = onVideoClick,
                )
            }
        }

        is StreamerDetailUiState.Error -> {
            ErrorState(
                messageRes = state.messageRes,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun StreamerDetailScreenPreview() {
    StrimupTheme {
        StreamerDetailScreen(
            onNavUp = {},
            onFavoriteClick = {},
            onVideoClick = { _, _ -> },
            onSocialClick = {},
            snackBarHostState = remember { SnackbarHostState() },
            state = StreamerDetailUiState.Success(
                streamer = Streamer(
                    id = "1",
                    isLive = true,
                    bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting. Je partage également toutes mes activités (Création graphique, montage vidéo)...",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezie",
                    tags = listOf(
                        TagEntity(id = 1, name = "Gaming", category = "dolk"),
                        TagEntity(id = 2, name = "Dev", category = "dolk")
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
                    followersCount = 10
                ),
                isFavorite = true
            ),
            onRetryClick = {},
            isReportAvailable = true,
            onReportClick = {},
        )
    }
}