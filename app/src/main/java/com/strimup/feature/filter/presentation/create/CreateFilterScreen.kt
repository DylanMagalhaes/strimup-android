package com.strimup.feature.filter.presentation.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.component.editrow.ProfileEditRow
import com.strimup.common.ui.component.editsBottomSheet.MultipleSelectBottomSheet
import com.strimup.common.ui.component.editsBottomSheet.SingleSelectBottomSheet
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.common.ui.component.editsBottomSheet.EditTextBottomSheet

// TODO: Remplacer ces listes fictives par tes vraies options/UseCases plus tard
private val TODO_PERSONALITIES = listOf("Tryhard", "Chill", "Pédagogue", "Drôle", "Compétitif")
private val TODO_STREAM_FREQUENCIES = listOf("Tous les jours", "Régulier (3-4/semaine)", "Occasionnel")
private val TODO_AVERAGE_VIEWERS = listOf("0 - 10", "10 - 50", "50 - 200", "200+")
private val TODO_LANGUAGES = listOf("Français", "Anglais", "Espagnol", "Allemand")
private val TODO_PLATFORMS = listOf("Twitch", "YouTube", "Kick")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFilterScreen(
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateFilterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Créer ton filtre",
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    if (state.isSubmitting) {
                        CircularProgressIndicator()
                    } else {
                        TextButton(
                            onClick = { /* TODO: viewModel.saveFilter() */ },
                            enabled = !state.isSubmitting
                        ) {
                            Text(
                                text = "Enregistrer",
                                fontFamily = zalandoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
    ) { padding ->
        CreateFilterContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
            onEditFilterNameClicked = { viewModel.openEdit(ActiveEditType.FilterName) },
            onEditPersonalitiesClicked = { viewModel.openEdit(ActiveEditType.Personalities) },
            onEditStreamFrequencyClicked = { viewModel.openEdit(ActiveEditType.StreamFrequency) },
            onEditAverageViewersClicked = { viewModel.openEdit(ActiveEditType.AverageViewers) },
            onEditLanguagesClicked = { viewModel.openEdit(ActiveEditType.Languages) },
            onEditPlatformsClicked = { viewModel.openEdit(ActiveEditType.Platforms) }
        )

        when (state.activeEdit) {
            ActiveEditType.FilterName -> {
                EditTextBottomSheet(
                    title = "Nom du filtre",
                    currentText = state.filterName,
                    onDone = { filterName ->
                        viewModel.onFilterNameChange(filterName)
                        viewModel.dismissEdit()
                    },
                    onDismiss = { viewModel.dismissEdit() },
                    description = "Donne un nom clair à ton filtre"
                )
            }

            ActiveEditType.Personalities -> {
                MultipleSelectBottomSheet(
                    title = "Personnalités",
                    options = TODO_PERSONALITIES,
                    selectedOptions = state.criteria.personalities,
                    onOptionSelected = { personality ->
                        viewModel.onPersonalitySelected(personality)
                    },
                    onDismiss = { viewModel.dismissEdit() }
                )
            }

            ActiveEditType.StreamFrequency -> {
                SingleSelectBottomSheet(
                    title = "Fréquence de stream",
                    options = TODO_STREAM_FREQUENCIES,
                    selectedOption = state.criteria.streamFrequency,
                    onOptionSelected = { newFrequency ->
                        viewModel.onStreamFrequencySelected(newFrequency)
                        viewModel.dismissEdit()
                    },
                    onDismiss = { viewModel.dismissEdit() }
                )
            }

            ActiveEditType.AverageViewers -> {
                SingleSelectBottomSheet(
                    title = "Nombre de viewers moyen",
                    options = TODO_AVERAGE_VIEWERS,
                    selectedOption = state.criteria.averageViewers,
                    onOptionSelected = { newAverage ->
                        viewModel.onAverageViewersSelected(newAverage)
                        viewModel.dismissEdit()
                    },
                    onDismiss = { viewModel.dismissEdit()}
                )
            }

            ActiveEditType.Languages -> {
                MultipleSelectBottomSheet(
                    title = "Langues",
                    options = TODO_LANGUAGES,
                    selectedOptions = state.criteria.languages,
                    onOptionSelected = { language ->
                        viewModel.onLanguagesSelected(language)
                    },
                    onDismiss = { viewModel.dismissEdit() }
                )
            }

            ActiveEditType.Platforms -> {
                MultipleSelectBottomSheet(
                    title = "Plateformes",
                    options = TODO_PLATFORMS,
                    selectedOptions = state.criteria.platforms,
                    onOptionSelected = { platform ->
                        viewModel.onPlatformSelected(platform)
                    },
                    onDismiss = { viewModel.dismissEdit() }
                )
            }

            ActiveEditType.AgeRange -> {
                // TODO: Implémenter le BottomSheet pour AgeRange si nécessaire
            }

            null -> {}
        }
    }
}

@Composable
fun CreateFilterContent(
    state: UiState,
    onEditFilterNameClicked: () -> Unit,
    onEditPersonalitiesClicked: () -> Unit,
    onEditStreamFrequencyClicked: () -> Unit,
    onEditAverageViewersClicked: () -> Unit,
    onEditLanguagesClicked: () -> Unit,
    onEditPlatformsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Informations Générales",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    ProfileEditRow(
                        label = "Nom du filtre",
                        value = state.filterName.ifEmpty { "Non renseigné" },
                        onClick = onEditFilterNameClicked
                    )
                }
            }
        }

        item {
            Text(
                text = "Critères de recherche",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    ProfileEditRow(
                        label = "Personnalité",
                        value = state.criteria.personalities.ifEmpty { listOf("Non renseigné") }.joinToString(", "),
                        onClick = onEditPersonalitiesClicked
                    )
                    ProfileEditRow(
                        label = "Fréquence de stream",
                        value = state.criteria.streamFrequency.ifEmpty { "Non renseigné" },
                        onClick = onEditStreamFrequencyClicked
                    )
                    ProfileEditRow(
                        label = "Nombre de viewers moyen",
                        value = state.criteria.averageViewers.ifEmpty { "Non renseigné" },
                        onClick = onEditAverageViewersClicked
                    )
                    ProfileEditRow(
                        label = "Langues",
                        value = state.criteria.languages.ifEmpty { listOf("Non renseigné") }.joinToString(", "),
                        onClick = onEditLanguagesClicked
                    )
                    ProfileEditRow(
                        label = "Plateformes",
                        value = state.criteria.platforms.ifEmpty { listOf("Non renseigné") }.joinToString(", "),
                        onClick = onEditPlatformsClicked
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateFilterScreenPreview() {
    StrimupTheme {
        CreateFilterContent(
            state = UiState(
                filterName = "Mon Filtre LoL",
                criteria = FilterCriteria(
                    personalities = listOf("Chill", "Tryhard"),
                    streamFrequency = "Régulier",
                    averageViewers = "10 - 50",
                    languages = listOf("Français"),
                    platforms = listOf("Twitch")
                )
            ),
            modifier = Modifier.fillMaxSize(),
            onEditFilterNameClicked = {},
            onEditPersonalitiesClicked = {},
            onEditStreamFrequencyClicked = {},
            onEditAverageViewersClicked = {},
            onEditLanguagesClicked = {},
            onEditPlatformsClicked = {}
        )
    }
}