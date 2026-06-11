package com.strimup.feature.home.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.domain.entity.StreamerEntity.Social
import com.strimup.feature.home.presentation.component.HomeTabs
import com.strimup.feature.home.presentation.component.StreamerCard
import com.strimup.feature.home.presentation.model.HomeTab

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        state = state,
        onStreamerSocialClick = { TODO() },
        onStreamerFavoriteClick = { TODO() },
        onTabClick = { viewModel.onTabClick(it) }
    )
}

@Composable
private fun HomeScreen(
    state: UiState,
    onStreamerFavoriteClick: (StreamerEntity) -> Unit,
    onStreamerSocialClick: (Social) -> Unit,
    onTabClick: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.padding(top = 32.dp)) {
            HomeTabs(
                modifier = Modifier.fillMaxWidth(),
                onButtonClick = onTabClick,
                currentTab = state.currentTab,
            )

            Crossfade(targetState = state.loading) { loading ->
                if (loading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(items = state.streamers) { streamer ->
                            StreamerCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 112.dp),
                                pseudo = streamer.userName,
                                socials = streamer.socials,
                                imageUrl = streamer.imageUrl,
                                saved = false, //Todo
                                isLive = streamer.isLive,
                                liveTitle = streamer.liveTitle,
                                onSocialClick = onStreamerSocialClick,
                                onFavoriteClick = { onStreamerFavoriteClick(streamer) },
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    StrimupTheme {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            state = UiState(),
            onStreamerFavoriteClick = {},
            onStreamerSocialClick = {},
            onTabClick = {}
        )
    }
}