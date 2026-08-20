package com.strimup.feature.filter.presentation.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.component.tag.SelectTagsContent
import com.strimup.core.ui.theme.StrimupTheme

@Composable
fun SelectFilterTagsScreen(
    viewModel: CreateFilterViewModel,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        when (val uiState = state) {
            is CreateFilterUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CreateFilterUiState.Content -> {
                SelectTagsContent(
                    title = "Tags du filtre",
                    description = "Sélectionne les tags recherchés pour cibler des streamers spécifiques.",
                    categories = uiState.availableCategories,
                    selectedCategory = uiState.selectedCategory,
                    tags = uiState.availableTags,
                    selectedTags = uiState.criteria.tags,
                    maxTags = 5,
                    onCategorySelected = viewModel::onCategorySelected,
                    onTagClick = viewModel::onTagSelected,
                    onDone = onNavUp,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectFilterTagsScreenPreview() {
    val sampleCategories = listOf(
        TagEntity(id = 1, category = "Gaming", name = "Multi Gaming"),
        TagEntity(id = 2, category = "IRL", name = "Discussion")
    )

    val sampleTags = listOf(
        TagEntity(id = 3, category = "Gaming", name = "FPS"),
        TagEntity(id = 4, category = "Gaming", name = "Chill"),
        TagEntity(id = 5, category = "Gaming", name = "Tryhard"),
        TagEntity(id = 6, category = "Gaming", name = "Coop")
    )

    StrimupTheme {
        Surface {
            SelectTagsContent(
                title = "Tags du filtre",
                description = "Sélectionne les tags recherchés pour cibler des streamers spécifiques.",
                categories = sampleCategories,
                selectedCategory = sampleCategories.first(),
                tags = sampleTags,
                selectedTags = listOf(sampleTags[0], sampleTags[1]),
                maxTags = 5,
                onCategorySelected = {},
                onTagClick = {},
                onDone = {}
            )
        }
    }
}