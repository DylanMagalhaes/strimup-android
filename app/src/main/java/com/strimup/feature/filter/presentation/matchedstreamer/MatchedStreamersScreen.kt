package com.strimup.feature.filter.presentation.matchedstreamer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.core.ui.component.streamer.StreamerCard
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun MatchedStreamersScreen(
    onNavUp: () -> Unit,
    onStreamerClick: (String) -> Unit,
    filterId: String,
    viewModel: MatchedStreamerListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current


    LaunchedEffect(filterId) {
        viewModel.initData(filterId)
    }

    MatchedStreamersScreen(
        state = state,
        onNavUp = onNavUp,
        onStreamerClick = onStreamerClick,
        onSocialClick = { socialUrl ->
            if (socialUrl != null) {
                uriHandler.openUri(socialUrl)
            }
        },
        onLoadNextPage = viewModel::loadNextPage,
        onLiveCheckedChange = viewModel::onLiveSwitch,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchedStreamersScreen(
    state: UiState,
    onNavUp: () -> Unit,
    onStreamerClick: (String) -> Unit,
    onSocialClick: (String?) -> Unit,
    onLoadNextPage: () -> Unit,
    onLiveCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.filterName ?: "Résultat filtre",
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
            onLiveCheckedChange = onLiveCheckedChange,
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
    onSocialClick: (String?) -> Unit,
    onLoadNextPage: () -> Unit,
    onLiveCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streamers = state.matchedResult?.streamers.orEmpty()
    val totalCount = state.matchedResult?.total ?: 0

    if (state.isLoading && streamers.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (!state.isLoading && streamers.isEmpty()) {
        EmptyMatchedStreamers(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "total_header") {
                TotalStreamersHeader(
                    total = totalCount,
                    isLive = state.isLiveOnly,
                    onLiveCheckedChange = onLiveCheckedChange,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            itemsIndexed(
                items = streamers,
                key = { _, streamer -> streamer.id }
            ) { index, streamer ->

                if (index >= streamers.size - 1) {
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
                    onSocialClick = onSocialClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isLoading && streamers.isNotEmpty()) {
                item(key = "pagination_loader") {
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
private fun TotalStreamersHeader(
    total: Int,
    isLive: Boolean,
    onLiveCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (total > 1) "streamers trouvés" else "streamer trouvé",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            onClick = onLiveCheckedChange,
            shape = CircleShape,
            color = if (isLive) Color(0xFF220000) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(
                width = 1.dp,
                color = if (isLive) Color(0xFFFF2E4D) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (isLive) Color(0xFFFF2E4D) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )

                Text(
                    text = "En live",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isLive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isLive) Color(0xFFFF2E4D) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyMatchedStreamers(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonSearch,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Aucun streamer trouvé",
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Essayez de modifier vos critères de recherche.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
@Preview
private fun MatchedStreamersScreenPreview() {
    StrimupTheme {
        MatchedStreamersScreen(
            onNavUp = {},
            onLiveCheckedChange = {},
            onStreamerClick = {},
            onSocialClick = {},
            onLoadNextPage = {},
            state = UiState(
                isLoading = false,
                isLiveOnly = true,
                matchedResult = StreamerMatchResult(
                    total = 230,
                    streamers = listOf(
                        Streamer(
                            id = "1",
                            userName = "squeezie",
                            imageUrl = "",
                            isLive = true,
                            socials = listOf(
                                Social(
                                    url = "https://twitch.tv/squeezie",
                                    type = Social.Type.Twitch
                                )
                            ),
                            liveTitle = "Live spécial !",
                            isFavorite = false,
                            tags = emptyList()
                        )
                    )
                )
            )
        )
    }
}