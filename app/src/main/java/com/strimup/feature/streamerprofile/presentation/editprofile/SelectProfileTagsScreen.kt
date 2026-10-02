package com.strimup.feature.streamerprofile.presentation.editprofile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.component.tag.SelectTagsContent
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme

private const val PROFILE_MAX_TAGS = 4

@Composable
fun SelectProfileTagsScreen(
    viewModel: EditProfileViewModel,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = screenTopWindowInsets,
    ) { innerPadding ->
        SelectTagsContent(
            title = stringResource(R.string.profile_tags_title),
            description = stringResource(R.string.profile_tags_description, PROFILE_MAX_TAGS),
            categories = state.availableCategories,
            selectedCategory = state.selectedCategory,
            tags = state.availableTags,
            selectedTags = state.selectedTags,
            maxTags = PROFILE_MAX_TAGS,
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
                title = stringResource(R.string.profile_tags_title),
                description = stringResource(R.string.profile_tags_description, PROFILE_MAX_TAGS),
                categories = sampleCategories,
                selectedCategory = sampleCategories.first(),
                tags = sampleTags,
                selectedTags = listOf(sampleTags[0]),
                maxTags = PROFILE_MAX_TAGS,
                onCategorySelected = {},
                onTagClick = {},
                onDone = {}
            )
        }
    }
}