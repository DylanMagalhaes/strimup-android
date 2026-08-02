package com.strimup.feature.streamerprofile.presentation.editeprofile

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.domain.entity.TagEntity
import com.strimup.feature.streamerprofile.presentation.component.ProfileEditRow
import com.strimup.feature.streamerprofile.presentation.editeprofile.component.EditProfileImageSection
import com.strimup.feature.streamerprofile.presentation.editeprofile.component.EditTextBottomSheet
import com.strimup.feature.streamerprofile.presentation.editeprofile.component.MultipleSelectBottomSheet
import com.strimup.feature.streamerprofile.presentation.editeprofile.component.SingleSelectBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavUp: () -> Unit,
    onEditTagsNav: (List<TagEntity>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) {
            onNavUp()
        }
    }

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (state.errorMessage != null && state.originalProfile == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = state.errorMessage ?: "Une erreur est survenue",
                color = MaterialTheme.colorScheme.error,
                fontFamily = zalandoFontFamily
            )
        }
    } else {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Modifier le profil",
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
                        if (state.isSaving) {
                            CircularProgressIndicator()
                        } else {
                            TextButton(
                                onClick = { viewModel.saveProfile() },
                                enabled = !state.isSaving
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
            EditProfileContent(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                state = state,
                onEditBioClicked = { viewModel.openEdit(ActiveEditType.Bio) },
                onEditDailyStatusClicked = { viewModel.openEdit(ActiveEditType.DailyStatus) },
                onEditPrimaryPersonalityClicked = { viewModel.openEdit(ActiveEditType.PrimaryPersonality) },
                onEditSecondaryPersonalityClicked = { viewModel.openEdit(ActiveEditType.SecondaryPersonality) },
                onEditStreamFrequencyClicked = { viewModel.openEdit(ActiveEditType.StreamFrequency) },
                onEditAverageViewersClicked = { viewModel.openEdit(ActiveEditType.AverageViewers) },
                onEditLanguagesClicked = { viewModel.openEdit(ActiveEditType.Languages) },
                onEditSocialClicked = { socialType ->
                    viewModel.openEdit(ActiveEditType.Social(socialType))
                },
                onImageSelected = { newPhoto ->
                    viewModel.onImageSelected(newPhoto)
                },
                onEditTagsClicked = { onEditTagsNav(state.selectedTags) }
            )

            val availableOptions = state.availableOptions ?: StreamerOptionsEntity(
                averageViewers = emptyList(),
                languages = emptyList(),
                personalities = emptyList(),
                streamFrequencies = emptyList()
            )

            when (val editType = state.activeEdit) {
                ActiveEditType.Bio -> {
                    EditTextBottomSheet(
                        title = "Modifier la bio",
                        currentText = state.bio,
                        onDone = { newBio ->
                            viewModel.onBioChanged(newBio)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() },
                        description = ""
                    )
                }

                ActiveEditType.DailyStatus -> {
                    EditTextBottomSheet(
                        title = "Modifier le statut du jour",
                        currentText = state.dailyStatus,
                        onDone = { newStatus ->
                            viewModel.onDailyStatusChanged(newStatus)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() },
                        description = ""
                    )
                }

                ActiveEditType.PrimaryPersonality -> {
                    val availablePersonalities = availableOptions.personalities.filter {
                        it != state.personalitySecondary
                    }

                    SingleSelectBottomSheet(
                        title = "Personnalité principale",
                        options = availablePersonalities,
                        selectedOption = state.personality,
                        onOptionSelected = { newPersonality ->
                            viewModel.onPrimaryPersonalityChanged(newPersonality)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() }
                    )
                }

                ActiveEditType.SecondaryPersonality -> {
                    val availablePersonalities = availableOptions.personalities.filter {
                        it != state.personality
                    }

                    SingleSelectBottomSheet(
                        title = "Personnalité secondaire",
                        options = availablePersonalities,
                        selectedOption = state.personalitySecondary,
                        onOptionSelected = { newPersonality ->
                            viewModel.onSecondaryPersonalityChanged(newPersonality)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() }
                    )
                }

                ActiveEditType.StreamFrequency -> {
                    SingleSelectBottomSheet(
                        title = "Fréquence de stream",
                        options = availableOptions.streamFrequencies,
                        selectedOption = state.streamFrequency,
                        onOptionSelected = { newFrequency ->
                            viewModel.onStreamFrequencyChanged(newFrequency)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() }
                    )
                }

                ActiveEditType.AverageViewers -> {
                    SingleSelectBottomSheet(
                        title = "Nombre de viewers moyen",
                        options = availableOptions.averageViewers,
                        selectedOption = state.averageViewers,
                        onOptionSelected = { newAverage ->
                            viewModel.onAverageViewersChanged(newAverage)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() }
                    )
                }

                is ActiveEditType.Languages -> {
                    MultipleSelectBottomSheet(
                        title = "Langues",
                        options = availableOptions.languages,
                        selectedOptions = state.selectedLanguages,
                        onOptionSelected = { language ->
                            viewModel.onLanguageSelected(language)
                        },
                        onDismiss = { viewModel.dismissEdit() },
                    )
                }

                is ActiveEditType.Social -> {
                    val existingUrl = state.socials.find { it.type == editType.type }?.url ?: ""
                    EditTextBottomSheet(
                        title = "Lien ${editType.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        currentText = existingUrl,
                        onDone = { newUrl ->
                            viewModel.onSocialUrlChanged(newUrl, editType.type)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() },
                        description = "Collez l'URL de votre compte ici"
                    )
                }

                null -> {}
            }
        }
    }
}

@Composable
fun EditProfileContent(
    state: EditProfileUiState,
    onEditBioClicked: () -> Unit,
    onEditDailyStatusClicked: () -> Unit,
    onEditPrimaryPersonalityClicked: () -> Unit,
    onEditSecondaryPersonalityClicked: () -> Unit,
    onEditStreamFrequencyClicked: () -> Unit,
    onEditAverageViewersClicked: () -> Unit,
    onEditLanguagesClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onEditSocialClicked: (StreamerProfileEntity.Social.Type) -> Unit,
    onEditTagsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Photo de profil",
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
                EditProfileImageSection(
                    imageUrl = state.imageUrl,
                    onImageSelected = onImageSelected,
                )
            }
        }

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
                        label = "Bio",
                        value = state.bio,
                        onClick = onEditBioClicked
                    )
                    ProfileEditRow(
                        label = "Statut du jour",
                        value = state.dailyStatus,
                        onClick = onEditDailyStatusClicked
                    )
                }
            }
        }

        item {
            Text(
                text = "Détails du profil",
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
                        label = "Mes tags",
                        value = state.selectedTags.map { it.name }.ifEmpty { listOf("Non renseigné") }.joinToString(
                            ", "
                        ),
                        onClick = onEditTagsClicked
                    )
                    ProfileEditRow(
                        label = "Personnalité principale",
                        value = state.personality ?: "Non renseigné",
                        onClick = onEditPrimaryPersonalityClicked
                    )
                    ProfileEditRow(
                        label = "Personnalité secondaire",
                        value = state.personalitySecondary ?: "Non renseigné",
                        onClick = onEditSecondaryPersonalityClicked
                    )
                    ProfileEditRow(
                        label = "Fréquence de stream",
                        value = state.streamFrequency ?: "Non renseigné",
                        onClick = onEditStreamFrequencyClicked
                    )
                    ProfileEditRow(
                        label = "Nombre de viewers moyen",
                        value = state.averageViewers ?: "Non renseigné",
                        onClick = onEditAverageViewersClicked
                    )
                    ProfileEditRow(
                        label = "Langues",
                        value = state.selectedLanguages.ifEmpty { listOf("Non renseigné") }.joinToString(
                            ", "
                        ),
                        onClick = onEditLanguagesClicked
                    )
                }
            }
        }

        item {
            Text(
                text = "Réseaux sociaux",
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
                    StreamerProfileEntity.Social.Type.entries.forEach { socialType ->
                        val existingSocial = state.socials.find { it.type == socialType }
                        ProfileEditRow(
                            label = socialType.name.lowercase().replaceFirstChar { it.uppercase() },
                            value = existingSocial?.url ?: "Non renseigné",
                            onClick = { onEditSocialClicked(socialType) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val mockState = EditProfileUiState(
        isLoading = false,
        bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting...",
        dailyStatus = "En live ce soir à 21h !",
        selectedLanguages = listOf("Français", "Anglais"),
        personality = "Chill",
        personalitySecondary = "Tryhard",
        streamFrequency = "3x par semaine",
        averageViewers = "10-50",
        imageUrl = "",
        selectedCategory = TagEntity(
            id = 1,
            category = "",
            name = ""
        ),
        availableOptions = StreamerOptionsEntity(
            averageViewers = emptyList(),
            languages = listOf("Français", "Anglais"),
            personalities = listOf("Chill", "Tryhard", "Drôle"),
            streamFrequencies = listOf("1-2x/semaine", "3x par semaine")
        )
    )

    StrimupTheme {
        EditProfileContent(
            state = mockState,
            modifier = Modifier.fillMaxSize(),
            onEditBioClicked = {},
            onEditDailyStatusClicked = {},
            onEditPrimaryPersonalityClicked = {},
            onEditSecondaryPersonalityClicked = {},
            onEditStreamFrequencyClicked = {},
            onEditAverageViewersClicked = {},
            onEditLanguagesClicked = {},
            onEditSocialClicked = {},
            onImageSelected = {},
            onEditTagsClicked = {},
        )
    }
}