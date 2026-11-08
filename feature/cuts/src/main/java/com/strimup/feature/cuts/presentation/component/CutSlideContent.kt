package com.strimup.feature.cuts.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.ui.component.embed.InlineEmbedPlayer
import com.strimup.core.ui.component.embed.supportsSoundControl
import com.strimup.core.ui.theme.BrandPink
import com.strimup.feature.cuts.R
import com.strimup.feature.cuts.domain.entity.Cut

private const val HEART_ANIMATION_MS = 700
private const val HEART_MAX_SCALE = 1.2f
private const val HEART_GROW_SPEED = 3f
private const val PLAY_ICON_ALPHA = 0.8f
private const val SCRIM_ALPHA = 0.6f
private val EMBED_AUTHOR_TOP_OFFSET = 64.dp
private val bottomScrim = Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = SCRIM_ALPHA)))

@Composable
internal fun CutSlideContent(
    cut: Cut,
    isActive: Boolean,
    isMuted: Boolean,
    isLikeEnabled: Boolean,
    onMuteClick: () -> Unit,
    onLikeClick: (isDoubleTap: Boolean) -> Unit,
    onReportClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPausedByUser by remember(cut.id) { mutableStateOf(false) }
    var heartBursts by remember { mutableIntStateOf(0) }
    val isPlaying = isActive && !isPausedByUser
    val embed = cut.video.embed
    val canPause = embed == null || embed.supportsSoundControl

    LaunchedEffect(isActive) {
        if (!isActive) isPausedByUser = false
    }

    Box(modifier = modifier.background(Color.Black)) {
        CutMedia(cut = cut, isActive = isActive, isPlaying = isPlaying, isMuted = isMuted)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(cut.id, canPause) {
                    detectTapGestures(
                        onTap = { if (canPause) isPausedByUser = !isPausedByUser },
                        onDoubleTap = {
                            heartBursts++
                            onLikeClick(true)
                        },
                    )
                },
        )

        if (isPausedByUser) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.cuts_play),
                tint = Color.White.copy(alpha = PLAY_ICON_ALPHA),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA / 2), CircleShape),
            )
        }

        if (heartBursts > 0) {
            HeartBurst(trigger = heartBursts, modifier = Modifier.align(Alignment.Center))
        }

        val actions: @Composable (Modifier) -> Unit = { actionsModifier ->
            CutActions(
                likesCount = cut.likesCount,
                isLiked = cut.isLikedByMe,
                isLikeEnabled = isLikeEnabled,
                isMuted = isMuted,
                hasSoundControl = canPause,
                onMuteClick = onMuteClick,
                onLikeClick = { onLikeClick(false) },
                onReportClick = onReportClick,
                modifier = actionsModifier,
            )
        }

        if (embed == null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(bottomScrim)
                    .padding(start = 16.dp, end = 8.dp, bottom = 16.dp, top = 48.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                CutInfo(cut = cut, onAuthorClick = onAuthorClick, modifier = Modifier.weight(1f))
                actions(Modifier)
            }
        } else {
            CutAuthorRow(
                author = cut.author,
                onClick = onAuthorClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = EMBED_AUTHOR_TOP_OFFSET),
            )
            actions(
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
            )
        }
    }
}

@Composable
private fun CutMedia(cut: Cut, isActive: Boolean, isPlaying: Boolean, isMuted: Boolean) {
    val embed = cut.video.embed
    val source = remember(cut.video) { cut.video.source() }

    when {
        embed != null && isActive -> InlineEmbedPlayer(
            embed = embed,
            isPlaying = isPlaying,
            isMuted = isMuted,
            modifier = Modifier.fillMaxSize(),
        )

        embed != null -> AsyncImage(
            model = cut.video.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        source is VideoSource.HostedFile -> CutHostedPlayer(
            url = source.url,
            isPlaying = isPlaying,
            isMuted = isMuted,
            modifier = Modifier.fillMaxSize(),
        )

        else -> Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun HeartBurst(trigger: Int, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(1f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(HEART_ANIMATION_MS))
    }

    val value = progress.value
    Icon(
        imageVector = Icons.Filled.Favorite,
        contentDescription = null,
        tint = BrandPink,
        modifier = modifier
            .size(120.dp)
            .scale(HEART_MAX_SCALE * minOf(1f, value * HEART_GROW_SPEED))
            .alpha(1f - value),
    )
}
