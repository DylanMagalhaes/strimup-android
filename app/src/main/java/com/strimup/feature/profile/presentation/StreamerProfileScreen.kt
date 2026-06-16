package com.strimup.feature.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity
import com.strimup.feature.profile.presentation.component.StreamerContent
import com.strimup.feature.profile.presentation.component.StreamerHero

@Composable
fun StreamerProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: StreamerProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StreamerProfileScreen(
        modifier = modifier,
        state = state,
    )
}

@Composable
fun StreamerProfileScreen(
    state: UiState,
    modifier: Modifier = Modifier
) {
    if (state.loading || state.streamer == null) {

        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

    } else {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
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
private fun StreamerProfileScreenPreview() {
    StrimupTheme {
        StreamerProfileScreen(
            state = com.strimup.feature.profile.presentation.UiState(
                loading = false,
                streamer = ProfileStreamerEntity(
                    isLive = false,
                    bio = "lorem ipsum doloreoji enierjfi nejr jhzunf hjznf hbuzhjfzbef hzef ",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezi",
                    tags = listOf(
                        ProfileStreamerEntity.Tag(name = "Gamming", category = "dolk"),
                        ProfileStreamerEntity.Tag(name = "Dev", category = "dolk"),
                        ProfileStreamerEntity.Tag(name = "Cuisine", category = "dolk"),
                        ProfileStreamerEntity.Tag(name = "Cuisine", category = "dolk"),
                    ),
                    dailyStatus = "Hello la compagnie",
                    videos = null,
                    socials = listOf(
                        ProfileStreamerEntity.Social(
                            url = "",
                            type = ProfileStreamerEntity.Social.Type.Twitch
                        ),
                        ProfileStreamerEntity.Social(
                            url = "",
                            type = ProfileStreamerEntity.Social.Type.Youtube
                        ),
                        ProfileStreamerEntity.Social(
                            url = "",
                            type = ProfileStreamerEntity.Social.Type.Instagram
                        ),
                        ProfileStreamerEntity.Social(
                            url = "",
                            type = ProfileStreamerEntity.Social.Type.Kick
                        ),
                    ),
                )
            )
        )
    }
}