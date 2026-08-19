package com.strimup.feature.filter.presentation.list.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterItemCard(
    filter: FilterEntity,
    onApply: () -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val allBadges = remember(filter.criteria) {
        buildList {
            filter.criteria.platforms.forEach { add(it) }
            filter.criteria.languages.forEach { add(it) }
            filter.criteria.personalities.forEach { add(it) }
            filter.criteria.tags.forEach { tag ->
                if (tag.name.isNotBlank()) add(tag.name)
            }
        }
    }

    val infoFooter = remember(filter.criteria) {
        listOfNotNull(
            filter.criteria.status.takeIf { it.isNotBlank() },
            filter.criteria.averageViewers.takeIf { it.isNotBlank() }?.let { "$it viewers" },
            filter.criteria.streamFrequency.takeIf { it.isNotBlank() },
            filter.criteria.ageRange.let { "${it.first}-${it.last} ans" }
        ).joinToString(" • ")
    }

    Surface(
        onClick = onApply,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = filter.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Options du filtre",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Supprimer",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onDelete(filter.id)
                            }
                        )
                    }
                }
            }

            if (allBadges.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allBadges.forEach { label ->
                        FilterBadge(label = label)
                    }
                }
            }

            if (infoFooter.isNotBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(end = 12.dp, top = 2.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = infoFooter,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun FilterItemCardPreview() {
    val mockFilter = FilterEntity(
        id = "1",
        name = "Filtres Récents",
        criteria = FilterCriteria(
            ageRange = 18..35,
            languages = listOf("Français", "Anglais"),
            platforms = listOf("Twitch", "YouTube"),
            personalities = listOf("Dynamique", "Compétitif"),
            tags = listOf(
                TagEntity(id = 1, name = "Tryhard", category = "Style"),
                TagEntity(id = 2, name = "Chill", category = "Ambiance")
            ),
            averageViewers = "100-500",
            streamFrequency = "Quotidien",
            status = "En ligne"
        ),
        userId = "user_123"
    )

    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            FilterItemCard(
                filter = mockFilter,
                onApply = {},
                onDelete = {}
            )
        }
    }
}