package com.strimup.feature.filter.presentation.matchedstreamer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.domain.entity.StreamerEntity
import com.strimup.common.ui.component.StreamerCard
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@Composable
fun MatchedStreamersScreen(
    onNavUp: () -> Unit,
    filterId: String,
    viewModel: MatchedStreamerListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(filterId) {
        viewModel.initData(filterId)
    }

    MatchedStreamersScreen(
        state = state,
        onNavUp = onNavUp,
        onStreamerClick = { streamerId ->
            TODO()
        },
        onSocialClick = { socialUrl ->
            TODO()
        },
        onLoadNextPage = viewModel::loadNextPage,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchedStreamersScreen(
    state: UiState,
    onNavUp: () -> Unit,
    onStreamerClick: (String) -> Unit,
    onSocialClick: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Résultat filtre",
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        MatchedStreamersContent(
            state = state,
            onStreamerClick = onStreamerClick,
            onSocialClick = onSocialClick,
            onLoadNextPage = onLoadNextPage,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun MatchedStreamersContent(
    state: UiState,
    onStreamerClick: (String) -> Unit,
    onSocialClick: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.loading && state.streamers.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(
                items = state.streamers,
                key = { _, streamer -> streamer.id }
            ) { index, streamer ->

                if (index >= state.streamers.size - 1) {
                    LaunchedEffect(index) {
                        onLoadNextPage()
                    }
                }

                StreamerCard(
                    pseudo = streamer.userName,
                    socials = streamer.socials,
                    imageUrl = streamer.imageUrl,
                    isLive = streamer.isLive,
                    liveTitle = streamer.liveTitle,
                    onClick = { onStreamerClick(streamer.id) },
                    onSocialClick = { social -> onSocialClick(social.url ?: "") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.loading && state.streamers.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun MatchedStreamersScreenPreview() {
    StrimupTheme {
        MatchedStreamersScreen(
            onNavUp = {},
            onStreamerClick = {},
            onSocialClick = {},
            onLoadNextPage = {},
            state = UiState(
                loading = false,
                streamers = listOf(
                    StreamerEntity(
                        id = "1",
                        userName = "squeezie",
                        imageUrl = "",
                        isLive = true,
                        socials = listOf(
                            StreamerEntity.Social(
                                url = "https://twitch.tv/squeezie",
                                type = StreamerEntity.Social.Type.Twitch
                            )
                        ),
                        liveTitle = "frrfrgrgrgrgrg regerg reg",
                        isFavorite = false,
                        tags = emptyList()
                    )
                )
            )
        )
    }
}