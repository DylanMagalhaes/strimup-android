package com.strimup.feature.streamerprofile.presentation.editprofile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.domain.entity.TagEntity
import com.strimup.common.ui.component.tag.SelectTagsContent
import com.strimup.common.ui.theme.StrimupTheme

@Composable
fun SelectProfileTagsScreen(
    viewModel: EditProfileViewModel,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        SelectTagsContent(
            title = "Vos tags de stream",
            description = "Choisissez jusqu'à 4 tags pour aider les viewers à découvrir votre contenu.",
            categories = state.availableCategories,
            selectedCategory = state.selectedCategory,
            tags = state.availableTags,
            selectedTags = state.selectedTags,
            maxTags = 4,
            onCategorySelected = { viewModel.onCategorySelected(it) },
            onTagClick = { viewModel.onTagSelected(it) },
            onDone = onNavUp,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectProfileTagsScreenPreview() {
    val sampleCategories = listOf(
        TagEntity(id = 1, category = "Gaming", name = "Multi Gaming"),
        TagEntity(id = 2, category = "IRL", name = "Discussion")
    )

    val sampleTags = listOf(
        TagEntity(id = 3, category = "Gaming", name = "FPS"),
        TagEntity(id = 4, category = "Gaming", name = "Chill"),
        TagEntity(id = 5, category = "Gaming", name = "Tryhard")
    )

    StrimupTheme {
        Surface {
            SelectTagsContent(
                title = "Vos tags de stream",
                description = "Choisissez jusqu'à 4 tags pour aider les viewers à découvrir votre contenu.",
                categories = sampleCategories,
                selectedCategory = sampleCategories.first(),
                tags = sampleTags,
                selectedTags = listOf(sampleTags[0]),
                maxTags = 4,
                onCategorySelected = {},
                onTagClick = {},
                onDone = {}
            )
        }
    }
}