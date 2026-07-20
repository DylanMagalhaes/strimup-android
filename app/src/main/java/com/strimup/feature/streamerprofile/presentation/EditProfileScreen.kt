package com.strimup.feature.streamerprofile.presentation

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
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.presentation.component.EditBioBottomSheet
import com.strimup.feature.streamerprofile.presentation.component.ProfileEditRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateToEditBio: () -> Unit,
    onNavigateToEditLanguages: () -> Unit,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var activeEditType by remember { mutableStateOf<ActiveEditType?>(null) }

    when (val currentState = state) {
        is EditProfileUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = zalandoFontFamily
                )
            }
        }

        EditProfileUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is EditProfileUiState.Success -> {
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
                        }
                    )
                },
            ) { padding ->
                EditProfileContent(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    state = currentState,
                    onNavigateToEditBio = { activeEditType = ActiveEditType.Bio },
                    onNavigateToEditLanguages = onNavigateToEditLanguages,
                )

                if (activeEditType is ActiveEditType.Bio) {
                    EditBioBottomSheet(
                        currentBio = currentState.bio,
                        onSave = { newBio ->
                            viewModel.onBioChanged(newBio)
                            activeEditType = null
                        },
                        onDismiss = { activeEditType = null }
                    )
                }


            }
        }
    }
}

@Composable
fun EditProfileContent(
    state: EditProfileUiState.Success,
    onNavigateToEditBio: () -> Unit,
    onNavigateToEditLanguages: () -> Unit,
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
                        label = "Bio",
                        value = state.bio,
                        onClick = onNavigateToEditBio
                    )
                    ProfileEditRow(
                        label = "Statut du jour",
                        value = state.dailyStatus,
                        onClick = onNavigateToEditBio
                    )
                }
            }
        }

        item {
            Text(
                text = "Détails de la chaîne",
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
                        label = "Personnalité principale",
                        value = state.personality ?: "Non renseigné",
                        onClick = onNavigateToEditLanguages
                    )
                    ProfileEditRow(
                        label = "Personnalité secondaire",
                        value = state.personalitySecondary ?: "Non renseigné",
                        onClick = onNavigateToEditLanguages
                    )
                    ProfileEditRow(
                        label = "Fréquence de stream",
                        value = state.streamFrequency ?: "Non renseigné",
                        onClick = onNavigateToEditLanguages
                    )
                    ProfileEditRow(
                        label = "Nombre de viewers moyen",
                        value = state.averageViewers ?: "Non renseigné",
                        onClick = onNavigateToEditLanguages
                    )
                    ProfileEditRow(
                        label = "Langues",
                        value = state.selectedLanguages.ifEmpty { listOf("Non renseigné") }.joinToString(
                            ", "
                        ),
                        onClick = onNavigateToEditLanguages
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

        }

        item {
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
                            label = socialType.name,
                            value = existingSocial?.url ?: "Non renseigné",
                            onClick = {}
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
    val mockSuccessState = EditProfileUiState.Success(
        originalProfile = StreamerProfileEntity(
            bio = "Bienvenue sur mon stream !",
            userName = "raziu",
            imageUrl = "",
            isLive = false,
            dailyStatus = "",
            socials = emptyList(),
            tags = emptyList(),
            videos = emptyList(),
            averageViewers = "",
            languages = emptyList(),
            personality = "",
            personalitySecondary = "",
            streamFrequency = "",
        ),
        availableOptions = StreamerOptionsEntity(
            averageViewers = emptyList(),
            languages = listOf("Français", "Anglais"),
            personalities = emptyList(),
            streamFrequencies = emptyList()
        ),
        bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting...",
        dailyStatus = "En live ce soir à 21h !",
        selectedLanguages = listOf("Français", "Anglais"),
        selectedTags = emptyList(),
        socials = emptyList(),
        personality = "Chill",
        personalitySecondary = "Tryhard",
        streamFrequency = "3x par semaine",
        averageViewers = "10-50"
    )

    StrimupTheme {
        EditProfileContent(
            state = mockSuccessState,
            modifier = Modifier.fillMaxSize(),
            onNavigateToEditBio = {},
            onNavigateToEditLanguages = {}
        )
    }
}