package com.strimup.feature.home.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.strimup.common.ui.component.TagBadge
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.entity.StreamerEntity
import com.strimup.feature.home.domain.entity.StreamerEntity.Social
import com.strimup.feature.home.presentation.component.HomeTabs
import com.strimup.feature.home.presentation.component.StreamerCard

@Composable
fun HomeScreen(
    onStreamerClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.text)
                }
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        state = state,
        snackBarHostState = snackBarHostState,
        onStreamerClick = onStreamerClick,
        onStreamerSocialClick = { TODO() },
        onStreamerFavoriteClick = { TODO() },
        onTabClick = { viewModel.onTabClick(it) },
        onSearchQueryChange = { newText ->
            viewModel.onSearchQueryChange(newText)
        },
        onFocusChanged = { viewModel.onFocusChanges() },
        onNavBackClick = { viewModel.onNavBackClick() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: UiState,
    snackBarHostState: SnackbarHostState,
    onStreamerClick: (id: String) -> Unit,
    onStreamerFavoriteClick: (StreamerEntity) -> Unit,
    onStreamerSocialClick: (Social) -> Unit,
    onTabClick: (FilterEntity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFocusChanged: () -> Unit,
    onNavBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && !state.isSearchMode) {
                                    onFocusChanged()
                                }
                            },
                        placeholder = { Text("Rechercher un streamer...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        },
                        singleLine = true
                    )
                },
                navigationIcon = {
                    if (state.isSearchMode) {
                        IconButton(onClick = onNavBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (state.isSearchMode) {
                SearchResult(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onStreamerClick = onStreamerClick
                )
            } else {
                HomeContent(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onStreamerClick = onStreamerClick,
                    onStreamerSocialClick = onStreamerSocialClick,
                    onStreamerFavoriteClick = onStreamerFavoriteClick,
                    onTabClick = onTabClick,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: UiState,
    onStreamerClick: (id: String) -> Unit,
    onStreamerFavoriteClick: (StreamerEntity) -> Unit,
    onStreamerSocialClick: (Social) -> Unit,
    onTabClick: (FilterEntity) -> Unit,
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
                        items(
                            items = state.streamers,
                            key = { streamer -> streamer.id }) { streamer ->
                            StreamerCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 112.dp),
                                pseudo = streamer.userName,
                                socials = streamer.socials,
                                imageUrl = streamer.imageUrl,
                                saved = false,
                                isLive = streamer.isLive,
                                liveTitle = streamer.liveTitle,
                                onClick = { onStreamerClick(streamer.id) },
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
private fun SearchResult(
    state: UiState,
    onStreamerClick: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
    ) {

        if (state.loadingSearch) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = state.streamersResultSearch,
                    key = { streamer -> streamer.id }) { streamer ->
                    Surface(
                        onClick = { onStreamerClick(streamer.id) },
                        modifier = modifier,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .padding(
                                    start = 16.dp, end = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(shape = RoundedCornerShape(8.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = .4f)
                                    ),
                                model = streamer.imageUrl,
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)

                            ) {
                                Text(
                                    text = streamer.userName,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = zalandoFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    streamer.tags?.forEach { tag ->
                                        TagBadge(tag = tag.name)
                                    }
                                }
                            }
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
//        HomeContent(
//            modifier = Modifier.fillMaxSize(),
//            state = UiState(),
//            onStreamerClick = {},
//            onStreamerFavoriteClick = {},
//            onStreamerSocialClick = {},
//            onTabClick = {},
//        )

    }
}
