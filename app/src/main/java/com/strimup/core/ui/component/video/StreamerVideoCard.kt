package com.strimup.core.ui.component.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.R
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.util.extractYouTubeVideoId

private const val VIDEO_ASPECT_RATIO = 16f / 9f
private const val UNAVAILABLE_ALPHA = 0.5f

@Composable
fun StreamerVideoCard(
    video: Streamer.Video,
    onClick: (Streamer.Video) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
) {
    val source = video.source()
    val isClickable = source !is VideoSource.Unavailable

    Column(
        modifier = modifier.width(width),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StreamerVideoPreview(
            source = source,
            title = video.title,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isClickable) { onClick(video) },
        )
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

@Composable
fun StreamerVideoPreview(
    source: VideoSource,
    title: String,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
) {
    Box(
        modifier = modifier
            .aspectRatio(VIDEO_ASPECT_RATIO)
            .clip(RoundedCornerShape(if (isCompact) 8.dp else 16.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        when (source) {
            is VideoSource.HostedFile -> {
                HostedVideoFrame(url = source.url, title = title)
                PreviewBadge(icon = Icons.Filled.PlayArrow, isCompact = isCompact)
            }

            is VideoSource.ExternalLink -> {
                val youTubeId = source.url.extractYouTubeVideoId()
                    .takeIf { source.platform == VideoSource.Platform.YOUTUBE }
                if (youTubeId != null) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/$youTubeId/hqdefault.jpg",
                        contentDescription = title,
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
}

@Composable
private fun HostedVideoFrame(url: String, title: String) {
    val frame by produceState<ImageBitmap?>(initialValue = null, url) {
        value = VideoFrameLoader.firstFrame(url)?.asImageBitmap()
    }

    frame?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    } ?: Icon(
        imageVector = Icons.Outlined.Movie,
        contentDescription = null,
        tint = Color.White.copy(alpha = UNAVAILABLE_ALPHA),
    )
}

@Composable
private fun PreviewBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isCompact: Boolean,
) {
    Box(
        modifier = Modifier
            .size(if (isCompact) 24.dp else 40.dp)
            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape),
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
private fun PlatformLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
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
