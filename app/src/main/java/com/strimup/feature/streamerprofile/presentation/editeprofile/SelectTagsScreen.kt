package com.strimup.feature.streamerprofile.presentation.editeprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.component.TagBadge
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerprofile.domain.entity.TagEntity
import com.strimup.feature.streamerprofile.presentation.editeprofile.EditProfileViewModel

@Composable
fun SelectTagsScreen(
    viewModel: EditProfileViewModel,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        SelectTagsContent(
            categories = state.availableCategories,
            selectedCategory = state.selectedCategory,
            tags = state.availableTags,
            selectedTags = state.selectedTags,
            onCategorySelected = { viewModel.onCategorySelected(it) },
            onTagClick = { viewModel.onTagSelected(it) },
            onDone = { onNavUp() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTagsContent(
    categories: List<TagEntity>,
    selectedCategory: TagEntity?,
    tags: List<TagEntity>,
    selectedTags: List<TagEntity>,
    onCategorySelected: (TagEntity) -> Unit = {},
    onTagClick: (TagEntity) -> Unit = {},
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .imePadding()
    ) {
        VerticalSpacer(16.dp)

        Text(
            text = "Vos tags de stream",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        VerticalSpacer(8.dp)

        Text(
            text = "Choisissez jusqu'à 4 tags pour aider les viewers à découvrir votre contenu.",
            style = MaterialTheme.typography.titleSmall,
            fontFamily = zalandoFontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        VerticalSpacer(16.dp)


        Text(
            text = "Tags sélectionnés (${selectedTags.size}/4) :",
            style = MaterialTheme.typography.labelLarge,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        VerticalSpacer(8.dp)

        if (selectedTags.isEmpty()) {
            Text(
                text = "Aucun tag sélectionné pour le moment",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = zalandoFontFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                selectedTags.forEach { selectedTag ->
                    TagBadge(
                        tag = selectedTag.name,
                        isSelected = true,
                        onTagClick = { onTagClick(selectedTag) }
                    )
                }
            }
        }

        VerticalSpacer(24.dp)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory?.category ?: "Choisissez une catégorie",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = "Sélectionner une catégorie",
                        fontFamily = zalandoFontFamily
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { tag ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = tag.category,
                                fontFamily = zalandoFontFamily,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onCategorySelected(tag)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        VerticalSpacer(16.dp)

        // 🟢 Tags disponibles de la catégorie active
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 4,
            modifier = Modifier.weight(1f)
        ) {
            tags.forEach { tag ->
                TagBadge(
                    tag = tag.name,
                    isSelected = selectedTags.contains(tag),
                    onTagClick = { onTagClick(tag) }
                )
            }
        }

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Terminer",
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SelectTagsScreenPreview() {
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
        SelectTagsContent(
            categories = sampleCategories,
            selectedCategory = sampleCategories.first(),
            tags = sampleTags,
            selectedTags = listOf(sampleTags[0]),
            onCategorySelected = {},
            onTagClick = {},
            onDone = {}
        )
    }
}