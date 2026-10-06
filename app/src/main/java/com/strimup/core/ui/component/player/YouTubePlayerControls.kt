package com.strimup.core.ui.component.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.strimup.R
import kotlinx.coroutines.delay

private const val SEEK_STEP_SECONDS = 10
private const val CONTROLS_AUTO_HIDE_MS = 3_000L
private const val SEEK_FEEDBACK_MS = 600L
private const val ICON_SIZE_RATIO = 0.6f

private enum class SeekDirection { BACKWARD, FORWARD }

@Composable
fun YouTubePlayerControls(
    state: YouTubePlayerState,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var isDragging by remember { mutableStateOf(false) }
    var interactionCount by remember { mutableIntStateOf(0) }
    var seekFeedback by remember { mutableStateOf<SeekDirection?>(null) }
    var seekFeedbackCount by remember { mutableIntStateOf(0) }

    fun registerInteraction() {
        controlsVisible = true
        interactionCount++
    }

    LaunchedEffect(controlsVisible, state.isPlaying, isDragging, interactionCount) {
        if (controlsVisible && state.isPlaying && !isDragging) {
            delay(CONTROLS_AUTO_HIDE_MS)
            controlsVisible = false
        }
    }

    LaunchedEffect(seekFeedbackCount) {
        if (seekFeedback != null) {
            delay(SEEK_FEEDBACK_MS)
            seekFeedback = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state) {
                    detectTapGestures(
                        onTap = {
                            if (controlsVisible) controlsVisible = false else registerInteraction()
                        },
                        onDoubleTap = { offset ->
                            val direction = if (offset.x < size.width / 2) {
                                SeekDirection.BACKWARD
                            } else {
                                SeekDirection.FORWARD
                            }
                            val delta = SEEK_STEP_SECONDS.toFloat()
                            state.seekBy(if (direction == SeekDirection.BACKWARD) -delta else delta)
                            seekFeedback = direction
                            seekFeedbackCount++
                            if (controlsVisible) registerInteraction()
                        },
                    )
                },
        )

        SeekFeedback(
            direction = seekFeedback,
            modifier = Modifier.fillMaxSize(),
        )

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .safeDrawingPadding(),
            ) {
                RoundIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = stringResource(R.string.action_close),
                    onClick = onClose,
                    size = 40.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                )

                RoundIconButton(
                    icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = stringResource(
                        if (state.isPlaying) R.string.action_pause else R.string.action_play,
                    ),
                    onClick = {
                        state.togglePlayPause()
                        registerInteraction()
                    },
                    size = 64.dp,
                    modifier = Modifier.align(Alignment.Center),
                )

                PlayerProgressBar(
                    currentTime = state.currentTime,
                    duration = state.duration,
                    onSeek = state::seekTo,
                    onDraggingChange = { dragging ->
                        isDragging = dragging
                        registerInteraction()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color.White.copy(alpha = 0.15f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size * ICON_SIZE_RATIO),
        )
    }
}

@Composable
private fun SeekFeedback(
    direction: SeekDirection?,
    modifier: Modifier = Modifier,
) {
    var lastDirection by remember { mutableStateOf(SeekDirection.FORWARD) }
    if (direction != null) lastDirection = direction

    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = direction != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(
                if (lastDirection == SeekDirection.BACKWARD) Alignment.CenterStart else Alignment.CenterEnd,
            ),
        ) {
            Text(
                text = stringResource(
                    if (lastDirection == SeekDirection.BACKWARD) {
                        R.string.player_seek_backward
                    } else {
                        R.string.player_seek_forward
                    },
                    SEEK_STEP_SECONDS,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
