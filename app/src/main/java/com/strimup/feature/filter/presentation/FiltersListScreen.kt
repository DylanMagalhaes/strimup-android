package com.strimup.feature.filter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import com.strimup.feature.filter.presentation.component.FilterItemCard

@Composable
fun FilterListScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    FiltersListContent(
        modifier = modifier,
        state = state,
        onApplyFilter = {},
        onDeleteFilter = {},
        onCreateFilterClick = {},
    )


}

@Composable
private fun FiltersListContent(
    state: UiState,
    onCreateFilterClick: () -> Unit,
    onApplyFilter: (FilterEntity) -> Unit,
    onDeleteFilter: (FilterEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateFilterClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Créer un filtre"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.filters.isEmpty() -> {
                    Text(
                        text = "Aucun filtre enregistré.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = state.filters,
                            key = { it.id }
                        ) { filter ->
                            FilterItemCard(
                                filter = filter,
                                onApply = { onApplyFilter(filter) },
                                onDelete = { onDeleteFilter(filter) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FiltersListPreview() {
    StrimupTheme {
        FiltersListContent(
            state = UiState(
                isLoading = false,
                filters = listOf(
                    FilterEntity(
                        id = "1",
                        name = "Gaming FPS",
                        criteria = FilterCriteria(),
                        userId = ""
                    ),
                    FilterEntity(
                        id = "2",
                        name = "Just Chatting Safe",
                        criteria = FilterCriteria(),
                        userId = ""
                    )
                )
            ),
            onCreateFilterClick = {},
            onApplyFilter = {},
            onDeleteFilter = {}
        )
    }
}