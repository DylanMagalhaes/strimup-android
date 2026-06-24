package com.strimup.feature.streamerdetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import com.strimup.feature.streamerdetail.presentation.component.StreamerContent
import com.strimup.feature.streamerdetail.presentation.component.StreamerHero

@Composable
fun StreamerDetailScreen(
    streamerId: String,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamerDetailViewModel = hiltViewModel(
        creationCallback = { factory: StreamerDetailViewModel.Factory -> factory.create(streamerId) }
    ),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    StreamerDetailScreen(
        modifier = modifier,
        state = state,
        onNavUp = onNavUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerDetailScreen(
    state: UiState,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = state.streamer?.userName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                })
        },
    ) { padding ->
        StreamerDetailContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
        )
    }
}

@Composable
private fun StreamerDetailContent(
    state: UiState,
    modifier: Modifier = Modifier,
) {
    if (state.loading || state.streamer == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            StreamerHero(
                modifier = Modifier.fillMaxWidth(),
                isLive = state.streamer.isLive,
                imageUrl = state.streamer.imageUrl,
                pseudo = state.streamer.userName,
                tags = state.streamer.tags,
                dailyStatus = state.streamer.dailyStatus,
            )

            StreamerContent(
                modifier = Modifier.fillMaxSize(),
                description = state.streamer.bio,
                socials = state.streamer.socials,
                onSocialClick = {},
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
            state = UiState(
                loading = false,
                streamer = StreamerDetailEntity(
                    isLive = false,
                    bio = "lorem ipsum doloreoji enierjfi nejr jhzunf hjznf hbuzhjfzbef hzef ",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezi",
                    tags = listOf(
                        StreamerDetailEntity.Tag(name = "Gamming", category = "dolk"),
                        StreamerDetailEntity.Tag(name = "Dev", category = "dolk"),
                        StreamerDetailEntity.Tag(name = "Cuisine", category = "dolk"),
                        StreamerDetailEntity.Tag(name = "Cuisine", category = "dolk"),
                    ),
                    dailyStatus = "Hello la compagnie",
                    videos = null,
                    socials = listOf(
                        StreamerDetailEntity.Social(
                            url = "",
                            type = StreamerDetailEntity.Social.Type.Twitch
                        ),
                        StreamerDetailEntity.Social(
                            url = "",
                            type = StreamerDetailEntity.Social.Type.Youtube
                        ),
                        StreamerDetailEntity.Social(
                            url = "",
                            type = StreamerDetailEntity.Social.Type.Instagram
                        ),
                        StreamerDetailEntity.Social(
                            url = "",
                            type = StreamerDetailEntity.Social.Type.Kick
                        ),
                    ),
                )
            )
        )
    }
}
