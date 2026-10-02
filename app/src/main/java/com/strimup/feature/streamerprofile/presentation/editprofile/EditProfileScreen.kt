package com.strimup.feature.streamerprofile.presentation.editprofile

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.ui.component.editrow.ProfileEditRow
import com.strimup.core.ui.component.editsBottomSheet.EditTextBottomSheet
import com.strimup.core.ui.component.editsBottomSheet.MultipleSelectBottomSheet
import com.strimup.core.ui.component.editsBottomSheet.SingleSelectBottomSheet
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.streamer.displayName
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerprofile.presentation.editprofile.component.EditProfileImageSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavUp: () -> Unit,
    onEditTagsNav: (List<TagEntity>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditProfileUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.message.asString(resources))
                }
                is EditProfileUiEvent.ProfileSaved -> {
                    onNavUp()
                }
            }
        }
    }

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (state.errorMessageRes != null && state.originalProfile == null) {
        ErrorState(
            messageRes = state.errorMessageRes ?: R.string.error_unknown,
            onRetryClick = viewModel::retry,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        Scaffold(
            modifier = modifier,
            contentWindowInsets = screenTopWindowInsets,
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.profile_edit_title),
                            fontFamily = zalandoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
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
                                    text = stringResource(R.string.action_save),
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
                    viewModel.openEdit(ActiveEditType.SocialEdit(socialType))
                },
                onImageSelected = { newPhoto ->
                    viewModel.onImageSelected(newPhoto)
                },
                onEditTagsClicked = { onEditTagsNav(state.selectedTags) }
            )

            val availableOptions = state.availableOptions ?: StreamerOptions(
                averageViewers = emptyList(),
                languages = emptyList(),
                personalities = emptyList(),
                streamFrequencies = emptyList()
            )

            when (val editType = state.activeEdit) {
                ActiveEditType.Bio -> {
                    EditTextBottomSheet(
                        title = stringResource(R.string.profile_edit_bio_title),
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
                        title = stringResource(R.string.profile_edit_status_title),
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
                        title = stringResource(R.string.profile_personality_primary),
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
                        title = stringResource(R.string.profile_personality_secondary),
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
                        title = stringResource(R.string.profile_stream_frequency),
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
                        title = stringResource(R.string.profile_average_viewers),
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
                        title = stringResource(R.string.profile_languages),
                        options = availableOptions.languages,
                        selectedOptions = state.selectedLanguages,
                        onOptionSelected = { language ->
                            viewModel.onLanguageSelected(language)
                        },
                        onDismiss = { viewModel.dismissEdit() },
                    )
                }

                is ActiveEditType.SocialEdit -> {
                    val existingUrl = state.socials.find { it.type == editType.type }?.url ?: ""
                    EditTextBottomSheet(
                        title = stringResource(R.string.profile_social_link_title, editType.type.displayName()),
                        currentText = existingUrl,
                        onDone = { newUrl ->
                            viewModel.onSocialUrlChanged(newUrl, editType.type)
                            viewModel.dismissEdit()
                        },
                        onDismiss = { viewModel.dismissEdit() },
                        description = stringResource(R.string.profile_social_link_hint)
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
    onEditSocialClicked: (Social.Type) -> Unit,
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
                text = stringResource(R.string.profile_photo_section),
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
                text = stringResource(R.string.profile_general_section),
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
                        label = stringResource(R.string.profile_bio),
                        value = state.bio,
                        onClick = onEditBioClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_daily_status),
                        value = state.dailyStatus,
                        onClick = onEditDailyStatusClicked
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.profile_details_section),
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
                        label = stringResource(R.string.profile_my_tags),
                        value = state.selectedTags.joinToString(", ") { it.name },
                        onClick = onEditTagsClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_personality_primary),
                        value = state.personality ?: stringResource(R.string.not_specified),
                        onClick = onEditPrimaryPersonalityClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_personality_secondary),
                        value = state.personalitySecondary ?: stringResource(R.string.not_specified),
                        onClick = onEditSecondaryPersonalityClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_stream_frequency),
                        value = state.streamFrequency ?: stringResource(R.string.not_specified),
                        onClick = onEditStreamFrequencyClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_average_viewers),
                        value = state.averageViewers ?: stringResource(R.string.not_specified),
                        onClick = onEditAverageViewersClicked
                    )
                    ProfileEditRow(
                        label = stringResource(R.string.profile_languages),
                        value = state.selectedLanguages.joinToString(", "),
                        onClick = onEditLanguagesClicked
                    )
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.profile_socials_section),
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
                    Social.Type.entries.forEach { socialType ->
                        val existingSocial = state.socials.find { it.type == socialType }
                        ProfileEditRow(
                            label = socialType.name.lowercase().replaceFirstChar { it.uppercase() },
                            value = existingSocial?.url ?: stringResource(R.string.not_specified),
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
        availableOptions = StreamerOptions(
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