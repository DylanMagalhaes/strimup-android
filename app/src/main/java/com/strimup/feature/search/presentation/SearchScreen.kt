package com.strimup.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.common.ui.component.TagBadge
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.home.presentation.UiState

@Composable
fun SearchScreen(
    state: UiState,
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
fun SearchScreenPreview(modifier: Modifier = Modifier) {
    StrimupTheme {
        SearchScreen()
    }
}