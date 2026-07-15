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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.component.StreamerHero
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import com.strimup.feature.streamerdetail.presentation.component.StreamerContent

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
                }
            )
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

    if (state.loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    else if (state.streamer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Impossible de charger les informations du streamer.")
        }
    }

    else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StreamerHero(
                modifier = Modifier.fillMaxWidth(),
                isLive = state.streamer.isLive,
                imageUrl = state.streamer.imageUrl,
                pseudo = state.streamer.userName,
                tags = state.streamer.tags?.map { it.name },
                dailyStatus = state.streamer.dailyStatus,
            )

            StreamerContent(
                modifier = Modifier.fillMaxWidth(),
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
                    isLive = true,
                    bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting. Je partage également toutes mes activités (Création graphique, montage vidéo)...",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezie",
                    tags = listOf(
                        StreamerDetailEntity.Tag(name = "Gaming", category = "dolk"),
                        StreamerDetailEntity.Tag(name = "Dev", category = "dolk")
                    ),
                    dailyStatus = "Hello la compagnie !",
                    videos = null,
                    socials = listOf(
                        StreamerDetailEntity.Social(url = "", type = StreamerDetailEntity.Social.Type.Twitch),
                        StreamerDetailEntity.Social(url = "", type = StreamerDetailEntity.Social.Type.Youtube)
                    ),
                )
            )
        )
    }
}