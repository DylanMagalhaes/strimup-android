package com.strimup.core.ui.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.LinkOff
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.ui.R
import com.strimup.core.util.extractYouTubeVideoId

private const val UNAVAILABLE_ALPHA = 0.5f
private const val OVERLAY_ALPHA = 0.6f

@Composable
fun StreamerVideoCard(
    video: Streamer.Video,
    position: Int,
    onClick: (Streamer.Video) -> Unit,
    modifier: Modifier = Modifier,
    horizontalWidth: Dp = 220.dp,
    verticalWidth: Dp = 124.dp,
) {
    val preview = rememberStreamerVideoPreview(video)

    Column(
        modifier = modifier.width(preview.width(horizontalWidth, verticalWidth)),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StreamerVideoPreview(
            preview = preview,
            position = position,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = preview.isPlayable) { onClick(video) },
        )
        if (video.title.isNotBlank()) {
            Text(
                text = video.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun StreamerVideoPreview(
    preview: StreamerVideoPreviewModel,
    modifier: Modifier = Modifier,
    position: Int? = null,
    isCompact: Boolean = false,
) {
    val embed = preview.video.embed

    Box(
        modifier = modifier
            .aspectRatio(preview.orientation.aspectRatio)
            .clip(RoundedCornerShape(if (isCompact) 8.dp else 16.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        when {
            embed != null -> EmbedPreview(
                embed = embed,
                thumbnailUrl = preview.video.thumbnailUrl,
                title = preview.video.title,
                isCompact = isCompact,
            )

            else -> SourcePreview(preview = preview, isCompact = isCompact)
        }

        if (position != null) {
            PositionBadge(
                position = position,
                isCompact = isCompact,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(if (isCompact) 4.dp else 8.dp),
            )
        }
    }
}

@Composable
private fun BoxScope.EmbedPreview(
    embed: VideoEmbed,
    thumbnailUrl: String?,
    title: String,
    isCompact: Boolean,
) {
    PlatformTile(provider = embed.provider, isCompact = isCompact)
    if (thumbnailUrl != null) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = title.ifBlank { null },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
    if (thumbnailUrl != null || !isCompact) {
        PreviewBadge(icon = Icons.Filled.PlayArrow, isCompact = isCompact)
    }
    if (!isCompact) {
        PlatformLabel(
            text = stringResource(embed.provider.labelRes),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
        )
    }
}

@Composable
private fun BoxScope.SourcePreview(
    preview: StreamerVideoPreviewModel,
    isCompact: Boolean,
) {
    when (val source = preview.source) {
        is VideoSource.HostedFile -> {
            HostedFrame(frame = preview.hostedFrame, title = preview.video.title)
            PreviewBadge(icon = Icons.Filled.PlayArrow, isCompact = isCompact)
        }

        is VideoSource.ExternalLink -> {
            val youTubeId = source.url.extractYouTubeVideoId()
                .takeIf { source.platform == VideoSource.Platform.YOUTUBE }
            if (youTubeId != null) {
                AsyncImage(
                    model = "https://i.ytimg.com/vi/$youTubeId/hqdefault.jpg",
                    contentDescription = preview.video.title.ifBlank { null },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            PreviewBadge(icon = Icons.AutoMirrored.Outlined.OpenInNew, isCompact = isCompact)
            if (!isCompact) {
                PlatformLabel(
                    text = source.platformLabel(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                )
            }
        }

        VideoSource.Unavailable -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LinkOff,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = UNAVAILABLE_ALPHA),
                )
                if (!isCompact) {
                    Text(
                        text = stringResource(R.string.video_link_unavailable),
                        color = Color.White.copy(alpha = UNAVAILABLE_ALPHA),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformTile(provider: VideoEmbed.Provider, isCompact: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(provider.brandColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(provider.iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(bottom = if (isCompact) 0.dp else 56.dp)
                .size(if (isCompact) 16.dp else 32.dp),
        )
    }
}

@Composable
private fun HostedFrame(frame: ImageBitmap?, title: String) {
    if (frame != null) {
        Image(
            bitmap = frame,
            contentDescription = title.ifBlank { null },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    } else {
        Icon(
            imageVector = Icons.Outlined.Movie,
            contentDescription = null,
            tint = Color.White.copy(alpha = UNAVAILABLE_ALPHA),
        )
    }
}

@Composable
private fun PreviewBadge(icon: ImageVector, isCompact: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isCompact) 24.dp else 40.dp)
            .background(Color.Black.copy(alpha = OVERLAY_ALPHA), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (isCompact) 14.dp else 22.dp),
        )
    }
}

@Composable
private fun PositionBadge(position: Int, isCompact: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(if (isCompact) 18.dp else 24.dp)
            .background(Color.Black.copy(alpha = OVERLAY_ALPHA), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = position.toString(),
            color = Color.White,
            style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PlatformLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(Color.Black.copy(alpha = OVERLAY_ALPHA), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun VideoSource.ExternalLink.platformLabel(): String = when (platform) {
    VideoSource.Platform.YOUTUBE -> stringResource(R.string.video_platform_youtube)
    VideoSource.Platform.TIKTOK -> stringResource(R.string.video_platform_tiktok)
    VideoSource.Platform.TWITCH -> stringResource(R.string.video_platform_twitch)
    VideoSource.Platform.INSTAGRAM -> stringResource(R.string.video_platform_instagram)
    VideoSource.Platform.KICK -> stringResource(R.string.video_platform_kick)
    VideoSource.Platform.OTHER -> host
}
