package com.strimup.feature.cuts.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.VideoSource
import com.strimup.core.streamer.domain.entity.source
import com.strimup.core.ui.component.embed.InlineEmbedPlayer
import com.strimup.core.ui.component.embed.supportsPlaybackControl
import com.strimup.feature.cuts.domain.entity.Cut

@Composable
fun CutVideoPlayer(
    cut: Cut,
    isLoaded: Boolean,
    isPlaying: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
    fillBounds: Boolean = false,
) {
    val embed = cut.video.embed
    val source = remember(cut.video) { cut.video.source() }

    when {
        embed != null && isLoaded && (isPlaying || embed.supportsPlaybackControl) -> InlineEmbedPlayer(
            embed = embed,
            isPlaying = isPlaying,
            isMuted = isMuted,
            modifier = modifier,
        )

        embed != null -> AsyncImage(
            model = cut.video.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )

        source is VideoSource.HostedFile -> CutHostedPlayer(
            url = source.url,
            isPlaying = isPlaying,
            isMuted = isMuted,
            modifier = modifier,
            fillBounds = fillBounds,
        )

        else -> Box(modifier = modifier)
    }
}
