package com.strimup.feature.streamerdetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.component.streamer.StreamerContent
import com.strimup.core.ui.component.streamer.StreamerHero
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun StreamerDetailScreen(
    streamerId: String,
    onNavUp: () -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamerDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(streamerId) {
        viewModel.loadStreamer(streamerId)
    }

    StreamerDetailScreen(
        modifier = modifier,
        state = state,
        onNavUp = onNavUp,
        onSocialClick = { socialUrl ->
            if (!socialUrl.isNullOrBlank()) {
                try {
                    uriHandler.openUri(socialUrl)
                } catch (_: Exception) {
                }
            }
        },
        onVideoClick = onVideoClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerDetailScreen(
    state: StreamerDetailUiState,
    onNavUp: () -> Unit,
    onSocialClick: (String?) -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
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
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { padding ->
        StreamerDetailContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
            onSocialClick = onSocialClick,
            onVideoClick = onVideoClick
        )
    }
}

@Composable
private fun StreamerDetailContent(
    state: StreamerDetailUiState,
    onSocialClick: (String?) -> Unit,
    onVideoClick: (videoId: String, isVertical: Boolean) -> Unit,
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
            ) {
                StreamerHero(
                    modifier = Modifier.fillMaxWidth(),
                    isLive = state.streamer.isLive,
                    imageUrl = state.streamer.imageUrl ?: "",
                    pseudo = state.streamer.userName,
                    tags = state.streamer.tags?.map { it.name },
                    dailyStatus = state.streamer.dailyStatus ?: "",
                    followersCount = state.streamer.followersCount,
                )

                StreamerContent(
                    modifier = Modifier.fillMaxWidth(),
                    description = state.streamer.bio ?: "",
                    socials = state.streamer.socials,
                    onSocialClick = onSocialClick,
                    videos = state.streamer.videos,
                    onVideoClick = onVideoClick,
                )
            }
        }

        is StreamerDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Impossible de charger les informations du streamer.")
            }
        }
    }
}

@Preview
@Composable
private fun StreamerDetailScreenPreview() {
    StrimupTheme {
        StreamerDetailScreen(
            onNavUp = {},
            onVideoClick = { _, _ -> },
            onSocialClick = {},
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
                )
            )
        )
    }
}