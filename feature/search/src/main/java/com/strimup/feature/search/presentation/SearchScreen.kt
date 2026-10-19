package com.strimup.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.search.R
import com.strimup.core.ui.R as CoreUiR

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onStreamerClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val recentStreamers by viewModel.recentStreamers.collectAsStateWithLifecycle()
    val liveStreamers by viewModel.liveStreamers.collectAsStateWithLifecycle()

    SearchScreen(
        modifier = modifier,
        state = state,
        recentStreamers = recentStreamers,
        liveStreamers = liveStreamers,
        onStreamerClick = { streamer ->
            viewModel.onStreamerClick(streamer)
            onStreamerClick(streamer.id)
        },
        onRemoveRecentStreamerClick = viewModel::onRemoveRecentStreamerClick,
        onSearchInputChange = viewModel::onSearchInputChange
    )
}

@Composable
private fun SearchScreen(
    state: SearchUiState,
    recentStreamers: List<Streamer>,
    liveStreamers: List<Streamer>,
    onSearchInputChange: (String) -> Unit,
    onStreamerClick: (Streamer) -> Unit,
    onRemoveRecentStreamerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                placeholder = { Text(stringResource(CoreUiR.string.search_placeholder)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            SearchContent(
                modifier = Modifier.weight(1f),
                state = state,
                recentStreamers = recentStreamers,
                liveStreamers = liveStreamers,
                onStreamerClick = onStreamerClick,
                onRemoveRecentStreamerClick = onRemoveRecentStreamerClick,
                onRetryClick = { onSearchInputChange(state.searchQuery) }
            )
        }
    }
}

@Composable
private fun SearchContent(
    state: SearchUiState,
    recentStreamers: List<Streamer>,
    liveStreamers: List<Streamer>,
    onStreamerClick: (Streamer) -> Unit,
    onRemoveRecentStreamerClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is SearchUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is SearchUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.search_no_result, state.searchQuery),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        is SearchUiState.Error -> {
            ErrorState(
                messageRes = state.messageRes,
                onRetryClick = onRetryClick,
                modifier = modifier.fillMaxSize(),
            )
        }
        is SearchUiState.Content if state.searchQuery.isBlank() -> {
            SearchSuggestions(
                modifier = modifier.fillMaxSize(),
                recentStreamers = recentStreamers,
                liveStreamers = liveStreamers,
                onStreamerClick = onStreamerClick,
                onRemoveClick = onRemoveRecentStreamerClick,
            )
        }
        is SearchUiState.Content -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = state.streamers,
                    key = { streamer -> streamer.id }
                ) { streamer ->
                    StreamerSearchCard(
                        streamer = streamer,
                        onClick = { onStreamerClick(streamer) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSuggestions(
    recentStreamers: List<Streamer>,
    liveStreamers: List<Streamer>,
    onStreamerClick: (Streamer) -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (liveStreamers.isNotEmpty()) {
            item {
                SuggestionTitle(text = stringResource(R.string.search_live_title))
            }
            item {
                LiveStreamersRow(
                    streamers = liveStreamers,
                    onStreamerClick = onStreamerClick,
                )
            }
        }

        if (recentStreamers.isNotEmpty()) {
            item {
                SuggestionTitle(text = stringResource(R.string.search_recent_title))
            }
        }

        items(
            items = recentStreamers,
            key = { streamer -> streamer.id }
        ) { streamer ->
            StreamerSearchCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .animateItem(),
                streamer = streamer,
                onClick = { onStreamerClick(streamer) },
                onRemoveClick = { onRemoveClick(streamer.id) },
            )
        }
    }
}

@Composable
private fun SuggestionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.titleSmall,
        fontFamily = zalandoFontFamily,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun LiveStreamersRow(
    streamers: List<Streamer>,
    onStreamerClick: (Streamer) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = streamers,
            key = { streamer -> streamer.id }
        ) { streamer ->
            LiveStreamerAvatar(
                streamer = streamer,
                onClick = { onStreamerClick(streamer) },
            )
        }
    }
}

@Composable
private fun LiveStreamerAvatar(
    streamer: Streamer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.width(72.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .1f)),
                model = streamer.imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            Text(
                text = streamer.userName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StreamerSearchCard(
    streamer: Streamer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRemoveClick: (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .1f)),
                model = streamer.imageUrl,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = streamer.userName,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                )
            }

            onRemoveClick?.let {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(
                            R.string.search_recent_remove_description,
                            streamer.userName
                        ),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    StrimupTheme {
        SearchScreen(
            modifier = Modifier.fillMaxSize(),
            onStreamerClick = {},
            onRemoveRecentStreamerClick = {},
            recentStreamers = emptyList(),
            liveStreamers = emptyList(),
            state = SearchUiState.Content(
                searchQuery = "Search",
                streamers = listOf(
                    Streamer(id = "1", userName = "Squeezie", imageUrl = ""),
                    Streamer(id = "2", userName = "Gotaga", imageUrl = ""),
                    Streamer(id = "3", userName = "Kameto", imageUrl = "")
                )
            ),
            onSearchInputChange = {}
        )
    }
}
