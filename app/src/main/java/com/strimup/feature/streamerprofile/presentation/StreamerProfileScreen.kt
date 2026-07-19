package com.strimup.feature.streamerprofile.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.common.ui.component.StreamerHero
import com.strimup.common.ui.component.button.SocialIconButton
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.presentation.mapper.getIconRes

@Composable
fun StreamerProfileScreen(
    streamerId: String?,
    modifier: Modifier = Modifier,
    onNavUp: () -> Unit,
    viewModel: StreamerProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StreamerProfileScreen(
        state = state,
        onNavUp = onNavUp,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerProfileScreen(
    state: ProfileUiState,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = state.streamer?.userName ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { padding ->
        StreamerProfileContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = state,
        )
    }
}

@Composable
private fun StreamerProfileContent(
    state: ProfileUiState,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (state.loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (state.streamer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Impossible de charger les informations du streamer.")
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StreamerHero(
                modifier = Modifier.fillMaxWidth(),
                isLive = state.streamer.isLive,
                imageUrl = state.streamer.imageUrl,
                pseudo = state.streamer.userName,
                tags = state.streamer.tags?.map { it.name },
                dailyStatus = state.streamer.dailyStatus ?: "",
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary ,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "Modifier le profil",
                            fontFamily = zalandoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    VerticalSpacer(24.dp)

                    if (state.streamer.socials.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.streamer.socials.forEach { social ->
                                SocialIconButton(
                                    iconRes = social.getIconRes(),
                                    onClick = { /* TODO : Gérer le clic réseau social */ }
                                )
                            }
                        }
                        VerticalSpacer(24.dp)
                    }

                    Text(
                        text = "À propos",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                    )

                    VerticalSpacer(8.dp)

                    state.streamer.bio?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .animateContentSize()
                                .clickable { isExpanded = !isExpanded }
                        )
                    }

                    state.streamer.bio?.length?.let {
                        if (it > 120) {
                            Text(
                                text = if (isExpanded) "Voir moins" else "Lire la suite",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clickable { isExpanded = !isExpanded }
                            )
                        }
                    }

                    VerticalSpacer(24.dp)
                }
            }
        }
    }
}

@Preview
@Composable
private fun StreamerProfileScreenPreview(modifier: Modifier = Modifier) {
    StrimupTheme {
        StreamerProfileScreen(
            state = ProfileUiState(
                loading = false,
                streamer = StreamerProfileEntity(
                    isLive = true,
                    bio = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting. Je partage également toutes mes activités (Création graphique, montage vidéo)...",
                    imageUrl = "https://media.gqmagazine.fr/photos/5e145005ac4b7e00082c6e5f/1:1/w_1125,h_1125,c_limit/thumbnail_squeezy-rap.jpg",
                    userName = "Squeezie",
                    tags = listOf(
                        StreamerProfileEntity.Tag(name = "Gaming", category = "dolk", id = 3),
                        StreamerProfileEntity.Tag(name = "Dev", category = "dolk", id = 34)
                    ),
                    dailyStatus = "Hello la compagnie !",
                    videos = null,
                    socials = listOf(
                        StreamerProfileEntity.Social(
                            url = "",
                            type = StreamerProfileEntity.Social.Type.Twitch
                        ),
                        StreamerProfileEntity.Social(
                            url = "",
                            type = StreamerProfileEntity.Social.Type.Youtube
                        )
                    ),
                    averageViewers = 4,
                    languages = emptyList(),
                    personality = "",
                    personalitySecondary = "",
                    streamFrequency = "",
                )
            ),
            onNavUp = {}
        )
    }
}