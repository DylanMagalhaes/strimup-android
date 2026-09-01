package com.strimup.feature.favorite.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun FavoriteStreamerScreen(
    viewModel: FavoriteStreamersViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FavoriteStreamerScreenContent(
        state = state,
        onSearchQueryChange = {it -> viewModel.onSearchQueryChange(it)},
        modifier = modifier
    )
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteStreamerScreenContent(
    state: FavoriteStreamersUiState,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mes Streamers",
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                placeholder = { Text("Rechercher un streamer...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.isEmpty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyFavoriteState()
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 88.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = state.filteredStreamers,
                            key = { it.id }
                        ) { streamer ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(170.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(
                                                MaterialTheme.colorScheme.onBackground.copy(alpha = .4f)
                                            )
                                            .then(
                                                if (streamer.isLive) {
                                                    Modifier.border(
                                                        width = 2.dp,
                                                        color = MaterialTheme.colorScheme.tertiary,
                                                        shape = RoundedCornerShape(24.dp)
                                                    )
                                                } else {
                                                    Modifier
                                                }
                                            ),
                                        model = streamer.imageUrl,
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null,
                                    )
                                    if (streamer.isLive) {
                                        Badge(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .offset(y = 6.dp),
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            content = {
                                                Text(
                                                    text = "Live",
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontFamily = zalandoFontFamily,
                                                    fontStyle = FontStyle.Italic,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            },
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = streamer.userName,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (streamer.isLive && !streamer.liveTitle.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = streamer.liveTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
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
private fun EmptyFavoriteState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Bookmark,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aucun streamer enregistré",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ajoutez des streamers à vos favoris pour les retrouver rapidement.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun FavoriteStreamerScreenPreview(modifier: Modifier = Modifier) {
    StrimupTheme {
        FavoriteStreamerScreenContent(
            state = FavoriteStreamersUiState(
                isLoading = false,
                searchQuery = "",
                favoriteStreamers = listOf(
                    Streamer(
                        id = "a",
                        userName = "raziu",
                        isLive = true,
                        liveTitle = "Chill & Gaming | Multi-gaming du soir",
                        imageUrl = ""
                    ),
                    Streamer(
                        id = "az",
                        userName = "Kameto",
                        isLive = false,
                        liveTitle = null,
                        imageUrl = ""
                    ),
                    Streamer(
                        id = "ae",
                        userName = "Xenorox",
                        isLive = true,
                        liveTitle = "RUSH RANG RADIANT EN LIVE ALLER VIENS LE SANG",
                        imageUrl = ""
                    ),
                    Streamer(
                        id = "ar",
                        userName = "balti",
                        isLive = false,
                        liveTitle = null,
                        imageUrl = ""
                    )
                ),
            ),
            onSearchQueryChange = {}
        )
    }
}