package com.strimup.feature.streamervideos.presentation.videossection

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.ui.component.spacer.VerticalSpacer
import com.strimup.core.ui.component.video.StreamerVideoPlayerHost
import com.strimup.core.ui.component.video.StreamerVideoPreview
import com.strimup.core.ui.component.video.rememberStreamerVideoOpener
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy

@Composable
fun VideosSection(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState? = null,
    viewModel: VideosSectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addVideoState by viewModel.addVideoState.collectAsStateWithLifecycle()
    var videoToDelete by rememberSaveable { mutableStateOf<String?>(null) }
    val resources = LocalResources.current
    val fallbackSnackBarHostState = remember { SnackbarHostState() }
    val videoOpener = rememberStreamerVideoOpener(snackBarHostState ?: fallbackSnackBarHostState)
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModel.onVideoPicked(uri?.toString())
    }

    LaunchedEffect(Unit) {
        viewModel.loadVideos()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is VideosSectionUiEvent.ShowMessage -> {
                    snackBarHostState?.showSnackbar(event.text.asString(resources))
                }
            }
        }
    }

    VideosSection(
        state = state,
        onRetryClick = viewModel::loadVideos,
        onAddClick = {
            videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        },
        onDeleteClick = { videoId -> videoToDelete = videoId },
        onVideoClick = videoOpener::open,
        modifier = modifier,
    )

    StreamerVideoPlayerHost(opener = videoOpener)

    if (addVideoState.isVisible) {
        AddVideoSheet(
            state = addVideoState,
            onTitleChange = viewModel::onTitleChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onUploadClick = viewModel::onUploadClick,
            onCancelUploadClick = viewModel::onCancelUploadClick,
            onDismiss = viewModel::onAddDismiss,
        )
    }

    val deletedVideo = (state as? VideosSectionUiState.Success)?.videos?.firstOrNull { it.id == videoToDelete }
    if (deletedVideo != null) {
        DeleteVideoDialog(
            videoTitle = deletedVideo.title,
            onConfirm = {
                viewModel.onDeleteConfirm(deletedVideo.id)
                videoToDelete = null
            },
            onDismiss = { videoToDelete = null },
        )
    }
}

@Composable
private fun VideosSection(
    state: VideosSectionUiState,
    onRetryClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (videoId: String) -> Unit,
    onVideoClick: (Streamer.Video) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.videos_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            if (state is VideosSectionUiState.Success) {
                Text(
                    text = stringResource(R.string.videos_counter, state.videos.size, VideoPolicy.MAX_VIDEOS),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        VerticalSpacer(8.dp)

        when (state) {
            is VideosSectionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }

            is VideosSectionUiState.Success -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.videos.isEmpty()) {
                        VideosMessage(messageRes = R.string.videos_empty)
                    }

                    state.videos.forEach { video ->
                        VideoRow(
                            video = video,
                            isDeleting = video.id in state.deletingVideoIds,
                            onDeleteClick = { onDeleteClick(video.id) },
                            onClick = { onVideoClick(video) },
                        )
                    }

                    Button(
                        onClick = onAddClick,
                        enabled = state.canAddVideo,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = stringResource(R.string.videos_add),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            is VideosSectionUiState.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VideosMessage(
                        messageRes = state.messageRes,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onRetryClick) {
                        Text(text = stringResource(R.string.action_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoRow(
    video: Streamer.Video,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val source = video.source()

    Surface(
        onClick = onClick,
        enabled = source !is VideoSource.Unavailable,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StreamerVideoPreview(
                source = source,
                title = video.title,
                isCompact = true,
                modifier = Modifier.width(96.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = video.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (video.description.isNotBlank()) {
                    Text(
                        text = video.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.videos_delete, video.title),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteVideoDialog(
    videoTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.videos_delete_confirm_title)) },
        text = { Text(text = stringResource(R.string.videos_delete_confirm_message, videoTitle)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.videos_delete_confirm_action),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun VideosMessage(
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(messageRes),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )
}

private val previewVideos = listOf(
    Streamer.Video(id = "1", title = "Best of GTA RP", description = "Les meilleurs moments", url = "", order = 1),
    Streamer.Video(id = "2", title = "Mon setup 2026", description = "", url = "", order = 2),
    Streamer.Video(id = "3", title = "Tournoi communautaire", description = "", url = "", order = 3),
)

@Preview
@Composable
internal fun VideosSectionPreview() {
    StrimupTheme {
        Surface {
            VideosSection(
                state = VideosSectionUiState.Success(videos = previewVideos.take(2), deletingVideoIds = setOf("2")),
                onRetryClick = {},
                onAddClick = {},
                onDeleteClick = {},
                onVideoClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun VideosSectionFullPreview() {
    StrimupTheme {
        Surface {
            VideosSection(
                state = VideosSectionUiState.Success(videos = previewVideos),
                onRetryClick = {},
                onAddClick = {},
                onDeleteClick = {},
                onVideoClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
internal fun VideosSectionEmptyPreview() {
    StrimupTheme {
        Surface {
            VideosSection(
                state = VideosSectionUiState.Success(videos = emptyList()),
                onRetryClick = {},
                onAddClick = {},
                onDeleteClick = {},
                onVideoClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
